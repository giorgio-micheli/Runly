package io.github.giorgiomicheli.run.network

import io.github.giorgiomicheli.core.data.networking.constructRoute
import io.github.giorgiomicheli.core.data.networking.delete
import io.github.giorgiomicheli.core.data.networking.get
import io.github.giorgiomicheli.core.data.networking.safeCall
import io.github.giorgiomicheli.core.domain.run.RemoteRunDataSource
import io.github.giorgiomicheli.core.domain.run.Run
import io.github.giorgiomicheli.core.domain.util.DataFailure
import io.github.giorgiomicheli.core.domain.util.EmptyResult
import io.github.giorgiomicheli.core.domain.util.Result
import io.github.giorgiomicheli.core.domain.util.map
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.append
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json

class KtorRemoteDataSource(
    private val httpClient: HttpClient
): RemoteRunDataSource {

    override suspend fun getRuns(): Result<List<Run>, DataFailure.Network> {
        return httpClient.get<List<RunDto>>(
            route = "/runs",
        ).map { runDtos ->
            runDtos.map { it.toRun() }
        }
    }

    // Multi-part request
    override suspend fun postRun(
        run: Run,
        mapPicture: ByteArray
    ): Result<Run, DataFailure.Network> {
        val createRunRequestJson = Json.encodeToString(run.toCreateRunRequest())

        val response = safeCall<RunDto> {
            httpClient.submitFormWithBinaryData(
                url = constructRoute("/run"),
                formData = formData {
                    append("MAP_PICTURE", mapPicture, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=mappicture.jpg")
                    })
                    append("RUN_DATA", createRunRequestJson, Headers.build {
                        append(HttpHeaders.ContentType, "text/plain")
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"RUN_DATA\"")
                    })
                }
            ) {
                method = HttpMethod.Post
            }
        }
        return response.map {
            it.toRun()
        }
    }

    override suspend fun deleteRun(id: String): EmptyResult<DataFailure.Network> {
        return httpClient.delete(
            route = "/run",
            queryParameters = mapOf(
                "id" to id
            )
        )
    }
}