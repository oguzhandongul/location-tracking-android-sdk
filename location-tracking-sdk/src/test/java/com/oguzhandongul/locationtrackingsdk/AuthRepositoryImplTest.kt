package com.oguzhandongul.locationtrackingsdk

import android.content.SharedPreferences
import com.oguzhandongul.locationtrackingsdk.data.local.repository.AuthRepositoryImpl
import com.oguzhandongul.locationtrackingsdk.data.remote.models.response.TokensResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryImplTest {

    @Mock
    private lateinit var sharedPrefsMock: SharedPreferences

    @Mock
    private lateinit var sharedPrefsEditorMock: SharedPreferences.Editor

    @InjectMocks
    private lateinit var repository: AuthRepositoryImpl

    @Test
    fun `saveTokens stores tokens correctly in SharedPreferences`() {
        val sampleTokens = TokensResponse("accessToken", "2024-09-23T18:10:00Z", "refreshToken")

        whenever(sharedPrefsMock.edit()).thenReturn(sharedPrefsEditorMock)
        whenever(sharedPrefsEditorMock.putString(anyString(), anyString())).thenReturn(sharedPrefsEditorMock)

        repository.saveTokens(sampleTokens)

        verify(sharedPrefsEditorMock).putString(AuthRepositoryImpl.KEY_ACCESS_TOKEN, "accessToken")
        verify(sharedPrefsEditorMock).putString(AuthRepositoryImpl.KEY_REFRESH_TOKEN, "refreshToken")
        verify(sharedPrefsEditorMock).putString(AuthRepositoryImpl.KEY_EXPIRES_AT, "2024-09-23T18:10:00Z")
        verify(sharedPrefsEditorMock).apply()
    }

    @Test
    fun `getTokens returns tokens when all values are present`() {
        val accessToken = "sampleAccessToken"
        val refreshToken = "sampleRefreshToken"
        val expiresAt = "2024-09-23T18:10:00Z"

        whenever(sharedPrefsMock.getString(AuthRepositoryImpl.KEY_ACCESS_TOKEN, null)).thenReturn(accessToken)
        whenever(sharedPrefsMock.getString(AuthRepositoryImpl.KEY_REFRESH_TOKEN, null)).thenReturn(refreshToken)
        whenever(sharedPrefsMock.getString(AuthRepositoryImpl.KEY_EXPIRES_AT, null)).thenReturn(expiresAt)

        val result = repository.getTokens()

        assertEquals(accessToken, result?.accessToken)
        assertEquals(refreshToken, result?.refreshToken)
        assertEquals(expiresAt, result?.expiresAt)
    }

    @Test
    fun `getTokens returns null when accessToken is missing`() {
        whenever(sharedPrefsMock.getString(AuthRepositoryImpl.KEY_ACCESS_TOKEN, null)).thenReturn(null)

        val result = repository.getTokens()
        assertNull(result)
    }

    @Test
    fun `getTokens returns null when refreshToken is missing`() {
        whenever(sharedPrefsMock.getString(AuthRepositoryImpl.KEY_REFRESH_TOKEN, null)).thenReturn(null)

        val result = repository.getTokens()
        assertNull(result)
    }

    @Test
    fun `isTokenExpired returns true when token is expired`() {
        val pastDateString = "2023-03-27T10:00:00Z"
        whenever(sharedPrefsMock.getString(AuthRepositoryImpl.KEY_EXPIRES_AT, null)).thenReturn(pastDateString)

        val isExpired = repository.isTokenExpired()

        assertTrue(isExpired)
    }

    @Test
    fun `isTokenExpired returns false when token is not expired`() {
        val futureDateString = "2030-12-15T12:30:00Z"
        whenever(sharedPrefsMock.getString(AuthRepositoryImpl.KEY_EXPIRES_AT, null)).thenReturn(futureDateString)

        val isExpired = repository.isTokenExpired()

        assertFalse(isExpired)
    }

    @Test
    fun `isTokenExpired returns false when expiresAt is missing`() {
        whenever(sharedPrefsMock.getString(AuthRepositoryImpl.KEY_EXPIRES_AT, null)).thenReturn(null)

        val isExpired = repository.isTokenExpired()

        assertFalse(isExpired)
    }
}