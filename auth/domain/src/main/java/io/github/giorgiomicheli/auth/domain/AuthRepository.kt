package io.github.giorgiomicheli.auth.domain

import io.github.giorgiomicheli.core.domain.util.DataFailure
import io.github.giorgiomicheli.core.domain.util.EmptyResult

interface AuthRepository {
    suspend fun register(email: String, password: String): EmptyResult<DataFailure.Network>
}