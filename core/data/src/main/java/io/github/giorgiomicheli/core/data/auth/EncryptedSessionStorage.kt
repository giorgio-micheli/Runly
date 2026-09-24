package io.github.giorgiomicheli.core.data.auth

import android.content.SharedPreferences
import androidx.work.impl.model.Preference
import io.github.giorgiomicheli.core.domain.AuthInfo
import io.github.giorgiomicheli.core.domain.SessionStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit
import kotlinx.serialization.json.Json

/*
    Here we are using the old EncryptedSharedPreferences which should be deprecated.
    It still works for this use case, but in the future we could migrate to an encrypted Datastore
 */

class EncryptedSessionStorage(
    private val sharedPreferences: SharedPreferences
) : SessionStorage {

    companion object {
        private const val KEY_AUTH_INFO = "KEY_AUTH_INFO"
    }

    override suspend fun get(): AuthInfo? {
        return withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(KEY_AUTH_INFO, null)
            json?.let {
                Json.decodeFromString<AuthInfoSerializable>(it).toAuthInfo()
            }
        }
    }

    override suspend fun set(info: AuthInfo?) {
        withContext(Dispatchers.IO) {
            if (info == null) {
                sharedPreferences.edit(commit = true) { remove(KEY_AUTH_INFO) }
                return@withContext
            }

            val json = Json.encodeToString(info.toAuthInfoSerializable())
            sharedPreferences.edit(commit = true) { putString(KEY_AUTH_INFO, json) }
        }
    }
}