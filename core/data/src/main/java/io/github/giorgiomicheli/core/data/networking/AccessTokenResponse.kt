package io.github.giorgiomicheli.core.data.networking

import kotlinx.serialization.Serializable
import java.sql.Timestamp

/*
    POJO that we deserialize from the http response from the backend, which should contain a
    new access token and its expirationTimestamp
 */
@Serializable
data class AccessTokenResponse(
    val accessToken: String,
    val expirationTimestamp: Long,
)
