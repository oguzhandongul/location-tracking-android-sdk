package com.oguzhandongul.locationtrackingsdk.data.remote.models.response

data class TokensResponse(
    val accessToken: String,
    val expiresAt: String,
    val refreshToken: String? = null // Nullable for the refresh token response
)