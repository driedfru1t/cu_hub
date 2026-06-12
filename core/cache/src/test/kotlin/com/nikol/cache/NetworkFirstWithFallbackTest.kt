package com.nikol.cache

import arrow.core.Either
import com.nikol.network.NetworkError
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkFirstWithFallbackTest {
    @Test
    fun `если есть сеть то возвращаем Either Right`() = runTest {
        val networkDto = "NetworkDto"
        val domainModel = "DomainModel"

        var isSaveCalled = false
        var isCacheQueried = false

        val result = networkFirstWithFallback(
            fetch = { Either.Right(networkDto) },
            saveFetchResult = { data ->
                isSaveCalled = true
                assertEquals(networkDto, data)
            },
            queryCache = {
                isCacheQueried = true
                "SomeCache"
            },
            mapDtoToDomain = { dto ->
                assertEquals(networkDto, dto)
                domainModel
            }
        )

        assertEquals(Either.Right(domainModel), result)
        assertTrue("Данные из сети должны быть сохранены", isSaveCalled)
        assertFalse("Кэш не должен запрашиваться если данные из сети", isCacheQueried)
    }

    @Test
    fun `если нет сети и кэша то возвращаем ошибку`() = runTest {
        val domainModel = "DomainModel"

        var isSaveCalled = false
        var isCacheQueried = false
        var isMapperCalled = false

        val result = networkFirstWithFallback<String, String>(
            fetch = { Either.Left(NetworkError.NotFound) },
            saveFetchResult = { _ ->
                isSaveCalled = true
            },
            queryCache = {
                isCacheQueried = true
                null
            },
            mapDtoToDomain = { _ ->
                isMapperCalled = true
                domainModel
            }
        )
        assertEquals(Either.Left(NetworkError.NotFound), result)
        assertTrue("Кэш должен запрашиваться если сеть дала сбой", isCacheQueried)
        assertFalse("Данные не долны быть сохраненны", isSaveCalled)
        assertFalse("Маппер не должен вызываться", isMapperCalled)
    }

    @Test
    fun `если нет сети но есть кэш, то надо вернуть успех из кэша`() = runTest {
        val domainModel = "DomainModel"

        var isSaveCalled = false
        var isCacheQueried = false
        var isMapperCalled = false

        val result = networkFirstWithFallback<String, String>(
            fetch = { Either.Left(NetworkError.NotFound) },
            saveFetchResult = { _ ->
                isSaveCalled = true
            },
            queryCache = {
                isCacheQueried = true
                "SomeCache"
            },
            mapDtoToDomain = { _ ->
                isMapperCalled = true
                domainModel
            }
        )
        assertEquals(Either.Right( "SomeCache"), result)
        assertTrue("Кэш должен запрашиваться если сеть дала сбой", isCacheQueried)
        assertFalse("Маппер не должен вызываться", isMapperCalled)
        assertFalse("Данные не долны быть сохраненны", isSaveCalled)
    }
}