package io.github.giorgiomicheli.core.domain.util

sealed interface Result<out D, out E : Failure> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Failure<out E: io.github.giorgiomicheli.core.domain.util.Failure>(val failure: E): Result<Nothing, E>
}

inline fun <T, E: Failure, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Failure -> Result.Failure(failure)
        is Result.Success -> Result.Success(map(data))
    }
}

typealias EmptyDataResult<E> = Result<Unit, E>

/**
 * Maps whatever the type T is, to Unit, so it returns a Result<Unit, E> which is then type aliased
 * to EmptyDataResult<E>
 */
fun <T, E: Failure> Result<T, E>.asEmptyDataResult(): EmptyDataResult<E> {
    return map { }
}