package com.nikol.auth_impl.viewModel

import android.util.Log
import com.nikol.auth_impl.mvi.intent.AuthIntent
import com.nikol.security.CuToken
import com.nikol.security.TokenManager
import com.nikol.security.YandexToken
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)

    override fun finished(description: Description) = Dispatchers.resetMain()
}


class AuthViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    lateinit var tokenRepo: TokenManager

    lateinit var fakeNav: AuthRouter
    lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        tokenRepo = mockk()
        fakeNav = mockk()
        viewModel = AuthViewModel(tokenRepo)
        viewModel.attachRouter(fakeNav)

        every { fakeNav.toYandexAuth() } just runs
        coEvery { tokenRepo.saveYandexToken(any()) } just runs
        coEvery { tokenRepo.saveCuToken(any()) } just runs

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        viewModel.detachRouter()
        unmockkAll()
    }

    @Test
    fun `нормальное поведение`() = runTest {

        val testYandexToken = YandexToken("yandex_oauth_token_123")
        val testCuToken = CuToken("central_university_token_456")

        val loginIntent = AuthIntent.LogIn(
            yandexToken = testYandexToken,
            cuToken = testCuToken
        )
        viewModel.setIntent(loginIntent)

        coVerify(exactly = 1) { tokenRepo.saveYandexToken(any()) }
        coVerify(exactly = 1) { tokenRepo.saveCuToken(any()) }
        verify(exactly = 1) { fakeNav.toYandexAuth() }
    }

    @Test
    fun `случайно передали оба null`() = runTest {
        viewModel.setIntent(AuthIntent.LogIn())

        coVerify(exactly = 0) { tokenRepo.saveYandexToken(any()) }
        coVerify(exactly = 0) { tokenRepo.saveCuToken(any()) }

        verify(exactly = 0) { fakeNav.toYandexAuth() }
    }
}