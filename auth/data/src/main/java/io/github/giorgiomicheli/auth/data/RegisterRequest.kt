package io.github.giorgiomicheli.auth.data

import kotlinx.serialization.Serializable

/*
    This data class needs to be declared int the data module because it's using a dependency on
    Kotlin Serialization. The all idea of having the two modules "data" and "domain" is that we
    should be able to swap implementation libraries without having to touch the domain layer.
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String
)
