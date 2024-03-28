package com.oguzhandongul.locationtrackingsdk.domain.repository

import com.oguzhandongul.locationtrackingsdk.data.remote.response.TokensResponse

interface AuthRepository {
    fun saveTokens(tokens: TokensResponse?)
    fun getTokens(): TokensResponse?
    fun clearTokens()
    fun isTokenExpired(): Boolean
}