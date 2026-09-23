package io.github.giorgiomicheli.core.data.networking

import io.github.giorgiomicheli.core.data.BuildConfig
import io.github.giorgiomicheli.core.data.networking.post
import io.github.giorgiomicheli.core.domain.AuthInfo
import io.github.giorgiomicheli.core.domain.SessionStorage
import io.github.giorgiomicheli.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

class HttpClientFactory(
    private val sessionStorage: SessionStorage
) {

    fun build(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                // Header with the API-KEY that will be present in all the http requests
                header("x-api-key", BuildConfig.API_KEY)
            }
            install(Auth) {
                bearer {
                    // We instruct Ktor on how and where to get the tokens that will need to attach
                    // to every http request
                    loadTokens {
                        val info = sessionStorage.get()
                        BearerTokens(
                            accessToken = info?.accessToken ?: "",
                            refreshToken = info?.refreshToken ?: ""
                        )
                    }
                    // Ktor will call this code when the access token is expired
                    /*
                        Ktor will know that the access token is expired when it'll make an http
                        request and it'll get a '401' response status code. The Auth plugin will
                        then automatically call this refreshToken block in order to make another
                        request, this time with the refresh token attached, and hopefully get
                        a brand new access token.
                     */
                    refreshTokens {
                        // Read the tokens that we have currently stored in sharedPreferences
                        val info = sessionStorage.get()
                        // New request using the refresh token
                        val response = client.post<AccessTokenRequest, AccessTokenResponse>(
                            route = "/accessToken",
                            body = AccessTokenRequest(
                                refreshToken = info?.refreshToken ?: "",
                                userID = info?.userID ?: ""
                            )
                        )

                        if (response is Result.Success) {
                            // We got a new access token
                            // Then update the local auth info in the SharedPreferences with the new access token
                            val newAuthInfo = AuthInfo(
                                accessToken = response.data.accessToken,
                                refreshToken = info?.refreshToken ?: "",
                                userID = info?.userID ?: ""
                            )
                            sessionStorage.set(newAuthInfo)

                            BearerTokens(
                                accessToken = newAuthInfo.accessToken,
                                refreshToken = newAuthInfo.refreshToken
                            )
                        } else {
                            BearerTokens(
                                accessToken = "",
                                refreshToken = ""
                            )
                        }
                    }
                }
            }
        }
    }
}