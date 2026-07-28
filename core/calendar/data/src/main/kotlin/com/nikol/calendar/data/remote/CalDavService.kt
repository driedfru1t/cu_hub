package com.nikol.calendar.data.remote

import arrow.core.raise.Raise
import com.nikol.network.BaseRemoteDataSource
import com.nikol.network.NetworkError
import com.nikol.network.di.qualifers.CuHttpClient.Yandex
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import org.w3c.dom.Node
import javax.inject.Inject
import com.nikol.network.di.qualifers.HttpClient as Http


sealed interface CalDavError {
    data class Network(val error: NetworkError) : CalDavError
    object XmlParsingFailed : CalDavError
}


sealed interface CalendarSyncDTO {

    data class Upsert(
        val href: String,
        val eTag: String,
        val calendarData: String,
    ) : CalendarSyncDTO

    data class Delete(
        val href: String,
    ) : CalendarSyncDTO
}

data class ResponseCalendarDTO(
    val events: List<CalendarSyncDTO>,
    val syncToken: String,
)

class CalDavService @Inject constructor(
    @param:Http(Yandex) private val httpClient: HttpClient, json: Json
) : BaseRemoteDataSource(json) {

    private inline fun <T> Raise<CalDavError>.safeParse(
        block: () -> T
    ): T = runCatching(block)
        .getOrElse {
            raise(CalDavError.XmlParsingFailed)
        }

    private fun syncCollectionBody(syncToken: String?): String = """
    <D:sync-collection
        xmlns:D="DAV:"
        xmlns:C="urn:ietf:params:xml:ns:caldav">

        ${
        if (syncToken == null)
            "<D:sync-token/>"
        else
            "<D:sync-token>$syncToken</D:sync-token>"
        }

        <D:prop>
            <D:getetag/>
            <C:calendar-data/>
        </D:prop>

    </D:sync-collection>
""".trimIndent()

    // TODO: тут стоит переписать на потоковый парсер иначе будет жопа на больших календарях
    suspend fun Raise<CalDavError>.discoverCalendars(path: String, syncToken: String?) =
        safeApiCall(
            apiCall = {
                httpClient.request(path) {
                    header("Depth", 1)
                    method = HttpMethod("PROPFIND")
                    setBody(setBody(syncCollectionBody(syncToken)))
                }
            },
            mapError = { networkError -> CalDavError.Network(networkError) },
            transform = { xmlString ->
                safeParse {
                    val events =
                        xmlString.xpathNodes("//*[local-name()='response']").map { response ->
                            val href = response.evaluate("./*[local-name()='href']/text()")
                            val status = response.evaluate(
                                "normalize-space(./*[local-name()='status']/text())"
                            )

                            if (status == "HTTP/1.1 404 Not Found") {
                                CalendarSyncDTO.Delete(href)
                            } else {
                                CalendarSyncDTO.Upsert(
                                    href = href,
                                    eTag = response.evaluate(
                                        "./*[local-name()='propstat']/*[local-name()='prop']/*[local-name()='getetag']/text()"
                                    ),
                                    calendarData = response.evaluate(
                                        "./*[local-name()='propstat']/*[local-name()='prop']/*[local-name()='calendar-data']/text()"
                                    )
                                )
                            }
                        }

                    val syncToken = xmlString.xpathString(
                        "//*[local-name()='sync-token']/text()"
                    )

                    ResponseCalendarDTO(events, syncToken)
                }
            }
        )

    suspend fun Raise<CalDavError>.discoverPrincipals() = safeApiCall(
        apiCall = {
            httpClient.request {
                header("Depth", 1)
                method = HttpMethod("PROPFIND")
            }
        },
        mapError = { CalDavError.Network(it) },
        transform = { xml ->
            safeParse {
                xml.xpathString("//*[local-name()='current-user-principal']/*[local-name()='href']/text()")
            }
        }
    )

    suspend fun Raise<CalDavError>.discoverCalendarPath(path: String) = safeApiCall(
        apiCall = {
            httpClient.request(path) {
                header("Depth", 1)
                method = HttpMethod("PROPFIND")
                setBody(
                    """
                    <D:propfind xmlns:D="DAV:"
                                xmlns:C="urn:ietf:params:xml:ns:caldav">
                        <D:prop>
                            <C:calendar-home-set/>
                        </D:prop>
                    </D:propfind>
                """.trimIndent()
                )
            }
        },
        mapError = { CalDavError.Network(it) },
        transform = { xml ->
            safeParse {
                xml.xpathString("//*[local-name()='calendar-home-set']/*[local-name()='href']/text()")
            }
        }
    )

    suspend fun Raise<CalDavError>.getCalendarsPath(path: String) = safeApiCall(
        apiCall = {
            httpClient.request(path) {
                header("Depth", 1)
                method = HttpMethod("PROPFIND")
                setBody(
                    """
                        <D:propfind
                            xmlns:D="DAV:"
                            xmlns:C="urn:ietf:params:xml:ns:caldav">

                            <D:prop>
                                <D:displayname/>
                                <D:resourcetype/>
                                <C:supported-calendar-component-set/>
                            </D:prop>

                        </D:propfind>
                    """.trimIndent()
                )
            }
        },
        mapError = { CalDavError.Network(it) },
        transform = { xml ->
            safeParse {
                xml.xpathNodes(
                    """
                        //*[local-name()='response'][
                            ./*[local-name()='propstat'][
                                ./*[local-name()='status']='HTTP/1.1 200 OK'
                                and
                                ./*[local-name()='prop']/*[local-name()='resourcetype']/*[local-name()='calendar']
                            ]
                        ]/*[local-name()='href']/text()
                    """.trimIndent()
                ).map(Node::getTextContent)
            }
        }
    )
}