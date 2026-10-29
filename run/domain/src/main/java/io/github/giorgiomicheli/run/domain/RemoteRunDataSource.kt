package io.github.giorgiomicheli.run.domain

import io.github.giorgiomicheli.core.domain.run.Run
import io.github.giorgiomicheli.core.domain.util.DataFailure
import io.github.giorgiomicheli.core.domain.util.EmptyResult
import io.github.giorgiomicheli.core.domain.util.Result

interface RemoteRunDataSource {

    /**
     * Fetch new runs from the back-end
     * @return a list of runs if the api-call was successfull, a network failure otherwise.
     */
    suspend fun getRuns(): Result<List<Run>, DataFailure.Network>

    /**
     * Send a newly created run to be saved on the back-end
     * @param run the newly created run pojo
     * @param mapPicture the byte array of the picture of the map's run
     * @return the remotely created run if the api-call was successfull, a network failure otherwise.
     */
    suspend fun postRun(run: Run, mapPicture: ByteArray): Result<Run, DataFailure.Network>

    /**
     * Deletes a run both locally and remotely
     * @param id the id of the run to be deleted
     * @return A network failure if the api-call was not successfull
     */
    suspend fun deleteRun(id: String): EmptyResult<DataFailure.Network>

}