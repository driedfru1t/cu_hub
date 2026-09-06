package com.nikol.calendar.data.remote

import android.util.Xml
import arrow.core.raise.Raise
import arrow.core.raise.context.raise
import com.nikol.network.BaseRemoteDataSource
import com.nikol.network.NetworkError
import com.nikol.network.di.qualifers.CuHttpClient.Yandex
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.serialization.json.Json
import org.w3c.dom.Node
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import javax.inject.Inject
import com.nikol.network.di.qualifers.HttpClient as Http


sealed interface CalDavError {
    data class Network(val error: NetworkError) : CalDavError

    data class SyncTokenExpired(val path: String) : CalDavError
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

    context(raise: Raise<CalDavError>)
    suspend fun syncCalendars(
        path: String,
        syncToken: String?,
        saveBatch: suspend (List<CalendarSyncDTO>) -> Unit,
        saveToken: suspend (String) -> Unit
    ) {
        httpClient.prepareRequest(path) {
            header("Depth", 1)
            method = HttpMethod("REPORT")
            setBody(syncCollectionBody(syncToken))
        }.execute {
            when (it.status) {
                HttpStatusCode.MultiStatus -> {
                    raise.safeParse {
                        val inputStream = it.bodyAsChannel().toInputStream()
                        parseAndSaveCalendarXml(inputStream, saveBatch, saveToken)
                    }
                }

                HttpStatusCode.PreconditionFailed -> raise(CalDavError.SyncTokenExpired(path))
                else -> raise(CalDavError.Network(parseNetworkError(it.status.value)))
            }
        }
    }

    private suspend fun parseAndSaveCalendarXml(
        inputStream: InputStream,
        saveBatch: suspend (List<CalendarSyncDTO>) -> Unit,
        saveToken: suspend (String) -> Unit
    ) {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
            setInput(inputStream, "UTF-8")
        }
        val batch = ArrayList<CalendarSyncDTO>(50)
        var finalSyncToken: String? = null

        var eventType = parser.eventType
        var currentHref: String? = null
        var currentIcsData: String? = null
        var currentTag: String? = null
        var isDeleted = false

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "response" -> {
                            currentHref = null
                            currentIcsData = null
                            isDeleted = false
                            currentTag = null
                        }

                        "href" -> currentHref = parser.nextText()
                        "calendar-data" -> currentIcsData = parser.nextText()
                        "status" -> {
                            val status = parser.nextText()
                            if (status.contains("404")) {
                                isDeleted = true
                            }
                        }

                        "sync-token" -> finalSyncToken = parser.nextText()
                        "getetag" -> currentTag = parser.nextText()
                    }
                }

                XmlPullParser.END_TAG -> {
                    if (parser.name == "response" && currentHref != null) {
                        val item = if (isDeleted) {
                            CalendarSyncDTO.Delete(currentHref)
                        } else if (currentTag != null && currentIcsData != null) {
                            CalendarSyncDTO.Upsert(
                                href = currentHref,
                                eTag = currentTag,
                                calendarData = currentIcsData
                            )
                        } else null

                        item?.let { batch.add(it) }
                        if (batch.size == 50) {
                            saveBatch(batch.toList())
                            batch.clear()
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        if (batch.isNotEmpty()) {
            saveBatch(batch.toList())
            batch.clear()
        }

        finalSyncToken?.let { saveToken(it) }
    }

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