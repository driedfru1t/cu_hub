package com.nikol.cache

import app.cash.turbine.test
import arrow.core.Either
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


class NetworkBoundResourceTest {
    @Test
    fun `сеть есть и сетевой запрос успешен - сначала отдаём кэш, а потом сеть`() = runTest {
        val networkDto = "Dto"
        val initialCache = "initCache"
        val newCache = "newCache"
        val dbFlow = MutableStateFlow(initialCache)

        var isDataSave = false

        networkBoundResource(
            query = { dbFlow },
            fetch = { Either.Right(networkDto) },
            saveFetchRequest = { dto ->
                isDataSave = true
                assertEquals(dto, networkDto)
                dbFlow.value = newCache
            },
            shouldFetch = { true }
        ).test {
            assertEquals(Either.Right(initialCache), awaitItem())
            assertEquals(Either.Right(newCache), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertTrue("Данные должны быть сохраненны", isDataSave)
    }
}