package io.github.giorgiomicheli.core.data.networking

import kotlinx.serialization.Serializable

/*
    POJO being sent to the backend when we need to get a new access token.
    It will be serialized in json format and included in the body of the http request.
 */
@Serializable
data class AccessTokenRequest(
    val refreshToken: String,
    val userID: String
)
