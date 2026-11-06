package io.github.giorgiomicheli.core.data.networking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
    POJO being sent to the backend when we need to get a new access token.
    It will be serialized in json format and included in the body of the http request.
 */
@Serializable
data class AccessTokenRequest(
    val refreshToken: String,
    // The backend expects "userId": with "userID" the request can't be parsed and returns 500
    @SerialName("userId")
    val userID: String
)
