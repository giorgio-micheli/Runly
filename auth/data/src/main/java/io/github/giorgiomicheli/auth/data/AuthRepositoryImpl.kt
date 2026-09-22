package io.github.giorgiomicheli.auth.data

import io.github.giorgiomicheli.auth.domain.AuthRepository
import io.github.giorgiomicheli.core.data.networking.post
import io.github.giorgiomicheli.core.domain.util.DataFailure
import io.github.giorgiomicheli.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient
) : AuthRepository {

    override suspend fun register(email: String, password: String): EmptyResult<DataFailure.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(
                email = email,
                password = password
            )
        )
    }

}