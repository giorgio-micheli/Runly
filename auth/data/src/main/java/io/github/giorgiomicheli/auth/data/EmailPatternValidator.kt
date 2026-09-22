package io.github.giorgiomicheli.auth.data

import android.util.Patterns
import io.github.giorgiomicheli.auth.domain.PatternValidator

object EmailPatternValidator : PatternValidator {

    override fun matches(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }
}