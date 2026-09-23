package io.github.giorgiomicheli.core.domain

data class AuthInfo(
    val accessToken: String,
    val refreshToken: String,
    val userID: String
)
