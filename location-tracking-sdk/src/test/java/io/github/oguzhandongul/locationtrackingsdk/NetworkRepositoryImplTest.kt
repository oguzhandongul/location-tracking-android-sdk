package io.github.oguzhandongul.locationtrackingsdk

import io.github.oguzhandongul.locationtrackingsdk.core.models.SdkConfig
import io.github.oguzhandongul.locationtrackingsdk.data.exceptions.AuthenticationFailureException
import io.github.oguzhandongul.locationtrackingsdk.data.local.repository.AuthRepositoryImpl
import io.github.oguzhandongul.locationtrackingsdk.data.remote.api.ApiService
import io.github.oguzhandongul.locationtrackingsdk.data.remote.models.response.TokensResponse
import io.github.oguzhandongul.locationtrackingsdk.data.remote.repository.NetworkRepositoryImpl
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.whenever
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class NetworkRepositoryImplTest {

    @Mock
    private lateinit var config: SdkConfig

    @Mock
    private lateinit var authRepository: AuthRepositoryImpl

    @Mock
    private lateinit var apiService: ApiService

    private lateinit var repository: NetworkRepositoryImpl

    @Before
    fun setUp() {
        repository = NetworkRepositoryImpl(config, authRepository, apiService)
    }

    @Test
    fun `getInitialTokens saves tokens and logs on success`() = runTest {
        // Prepare mock data
        val mockTokensResponse = TokensResponse("accessToken", "2024-09-23T18:10:00Z", "refreshToken")
        val mockResponse = Response.success(200, mockTokensResponse)

        // Set up mocks
        whenever(config.apiKey).thenReturn("xdk8ih3kvw2c66isndihzke5")
        whenever(apiService.getNewTokens(anyString())).thenReturn(mockResponse)

        // Execute the method being tested
        repository.getInitialTokens()

        val inOrder = inOrder(authRepository)
        inOrder.verify(authRepository).saveTokens(mockTokensResponse)
    }

    @Test(expected = AuthenticationFailureException::class)
    fun `refreshAccessToken throws AuthenticationFailureException on 403 response`() = runTest {
        val mockResponse = Response.error<TokensResponse>(403, ResponseBody.Companion.create(null, ""))
        whenever(apiService.refreshAccessToken(anyString())).thenReturn(mockResponse)

        repository.refreshAccessToken("oldRefreshToken") // Code that is expected to throw the exception
    }

    @Test
    fun `refreshAccessToken handles invalid response body`() = runTest {
        val mockResponse = Response.success<TokensResponse>(null) // Null response body
        whenever(apiService.refreshAccessToken(anyString())).thenReturn(mockResponse)

        repository.refreshAccessToken("oldRefreshToken")

    }

    @Test
    fun `getInitialTokens throws Exception on unexpected API error`() = runTest {
        val mockResponse = Response.error<TokensResponse>(500, ResponseBody.Companion.create(null, ""))
        whenever(apiService.getNewTokens(anyString())).thenReturn(mockResponse)

        try {
            repository.getInitialTokens()
            fail("Exception was not thrown") // The test fails if we reach this point
        } catch (exception: Exception) {
            // Success! An Exception of the general type was thrown.
            // If needed, you could add assertions like: assertTrue(exception is ServerErrorException)
        }
    }
}