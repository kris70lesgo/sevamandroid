package com.sevam.core.realtime

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface SevamRealtimeClient {
    fun trackWorkerLocation(jobId: String): Flow<WorkerLocationUpdate>
}

data class SupabaseRealtimeConfig(
    val projectUrl: String,
    val anonKey: String,
    val schema: String = "public",
    val channelPrefix: String = "tracking",
    val workerLocationsTable: String = "worker_locations",
)

data class WorkerLocationUpdate(
    val jobId: String,
    val workerId: String,
    val latitude: Double?,
    val longitude: Double?,
    val timestampIso: String?,
    val eventType: WorkerLocationEventType,
)

enum class WorkerLocationEventType {
    INSERT,
    UPDATE,
    DELETE,
}

@Serializable
data class SupabaseWorkerLocationRecord(
    @SerialName("job_id")
    val jobId: String? = null,
    @SerialName("worker_id")
    val workerId: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("updated_at")
    val updatedAtIso: String? = null,
)

object SupabaseRealtimeMapper {
    fun toDomain(
        payload: SupabaseWorkerLocationRecord,
        eventType: WorkerLocationEventType,
    ): WorkerLocationUpdate {
        return WorkerLocationUpdate(
            jobId = payload.jobId.orEmpty(),
            workerId = payload.workerId.orEmpty(),
            latitude = payload.latitude,
            longitude = payload.longitude,
            timestampIso = payload.updatedAtIso,
            eventType = eventType,
        )
    }
}

class SupabaseRealtimeClient(
    private val config: SupabaseRealtimeConfig,
) : SevamRealtimeClient {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    private val supabaseClient: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = config.projectUrl,
            supabaseKey = config.anonKey,
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }

    fun channelNameForJob(jobId: String): String = "${config.channelPrefix}:job:$jobId"

    override fun trackWorkerLocation(jobId: String): Flow<WorkerLocationUpdate> {
        require(config.projectUrl.isNotBlank()) { "projectUrl must not be blank" }
        require(config.anonKey.isNotBlank()) { "anonKey must not be blank" }

        val channel = supabaseClient.channel(channelNameForJob(jobId))

        val insertFlow = channel
            .postgresChangeFlow<PostgresAction.Insert>(schema = config.schema) {
                table = config.workerLocationsTable
            }
            .mapNotNull { action -> parsePayload(action.record.toString(), WorkerLocationEventType.INSERT) }

        val updateFlow = channel
            .postgresChangeFlow<PostgresAction.Update>(schema = config.schema) {
                table = config.workerLocationsTable
            }
            .mapNotNull { action -> parsePayload(action.record.toString(), WorkerLocationEventType.UPDATE) }

        val deleteFlow = channel
            .postgresChangeFlow<PostgresAction.Delete>(schema = config.schema) {
                table = config.workerLocationsTable
            }
            .mapNotNull { action -> parsePayload(action.oldRecord.toString(), WorkerLocationEventType.DELETE) }

        return merge(insertFlow, updateFlow, deleteFlow)
            .filter { update -> update.jobId == jobId }
    }

    private fun parsePayload(
        payloadJson: String,
        eventType: WorkerLocationEventType,
    ): WorkerLocationUpdate? {
        val payload = runCatching {
            json.decodeFromString<SupabaseWorkerLocationRecord>(payloadJson)
        }.getOrNull() ?: return null

        if (payload.jobId.isNullOrBlank() || payload.workerId.isNullOrBlank()) {
            return null
        }

        return SupabaseRealtimeMapper.toDomain(payload, eventType)
    }
}
