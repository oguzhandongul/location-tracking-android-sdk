package com.oguzhandongul.locationtrackingsdk.domain.repository

import com.oguzhandongul.locationtrackingsdk.data.remote.models.response.TokensResponse

interface AuthRepository {
    fun saveTokens(tokens: TokensResponse?)
    fun getTokens(): TokensResponse?
    fun clearTokens()
    fun isTokenExpired(): Boolean
}