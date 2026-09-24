package io.github.giorgiomicheli.auth.domain

interface PatternValidator {
    fun matches(value: String): Boolean
}