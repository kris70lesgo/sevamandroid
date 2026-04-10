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
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive

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
    val latitude: Double,
    val longitude: Double,
    val timestampIso: String,
)

data class SupabaseWorkerLocationPayload(
    val jobId: String,
    val workerId: String,
    val latitude: Double,
    val longitude: Double,
    val updatedAtIso: String,
)

object SupabaseRealtimeMapper {
    fun toDomain(payload: SupabaseWorkerLocationPayload): WorkerLocationUpdate {
        return WorkerLocationUpdate(
            jobId = payload.jobId,
            workerId = payload.workerId,
            latitude = payload.latitude,
            longitude = payload.longitude,
            timestampIso = payload.updatedAtIso,
        )
    }
}

class SupabaseRealtimeClient(
    private val config: SupabaseRealtimeConfig,
) : SevamRealtimeClient {

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

        return channel
            .postgresChangeFlow<PostgresAction.Insert>(schema = config.schema) {
                table = config.workerLocationsTable
            }
            .mapNotNull { action -> parsePayload(action.record) }
            .filter { update -> update.jobId == jobId }
    }

    private fun parsePayload(record: JsonObject): WorkerLocationUpdate? {
        val jobId = record["job_id"]?.jsonPrimitive?.content ?: return null
        val workerId = record["worker_id"]?.jsonPrimitive?.content ?: return null
        val latitude = record["latitude"]?.jsonPrimitive?.doubleOrNull ?: return null
        val longitude = record["longitude"]?.jsonPrimitive?.doubleOrNull ?: return null
        val timestamp = record["updated_at"]?.jsonPrimitive?.content ?: return null

        return WorkerLocationUpdate(
            jobId = jobId,
            workerId = workerId,
            latitude = latitude,
            longitude = longitude,
            timestampIso = timestamp,
        )
    }
}
