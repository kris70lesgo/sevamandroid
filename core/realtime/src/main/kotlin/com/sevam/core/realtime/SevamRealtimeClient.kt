package com.sevam.core.realtime

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.broadcastFlow
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

interface SevamRealtimeClient {
    fun trackWorkerLocation(jobId: String): Flow<WorkerLocationUpdate>
}

data class SupabaseRealtimeConfig(
    val projectUrl: String,
    val anonKey: String,
    val channelPrefix: String = "job",
    val locationEvent: String = "WORKER_LOCATION",
    val usePrivateChannels: Boolean = true,
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
    BROADCAST,
}

@Serializable
data class WorkerLocationBroadcastPayload(
    @SerialName("lat")
    val latitude: Double? = null,
    @SerialName("lng")
    val longitude: Double? = null,
    @SerialName("ts")
    val timestampMillis: Long? = null,
)

object SupabaseRealtimeMapper {
    fun toDomain(
        jobId: String,
        payload: WorkerLocationBroadcastPayload,
    ): WorkerLocationUpdate {
        return WorkerLocationUpdate(
            jobId = jobId,
            workerId = "",
            latitude = payload.latitude,
            longitude = payload.longitude,
            timestampIso = payload.timestampMillis?.let { Instant.ofEpochMilli(it).toString() },
            eventType = WorkerLocationEventType.BROADCAST,
        )
    }
}

class SupabaseRealtimeClient(
    private val config: SupabaseRealtimeConfig,
    private val accessTokenProvider: suspend () -> String? = { null },
) : SevamRealtimeClient {

    private val supabaseClient: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = config.projectUrl,
            supabaseKey = config.anonKey,
        ) {
            install(Auth)
            install(Realtime)
        }
    }

    fun channelNameForJob(jobId: String): String = "${config.channelPrefix}:$jobId"

    override fun trackWorkerLocation(jobId: String): Flow<WorkerLocationUpdate> {
        require(config.projectUrl.isNotBlank()) { "projectUrl must not be blank" }
        require(config.anonKey.isNotBlank()) { "anonKey must not be blank" }

        val channel = supabaseClient.channel(channelNameForJob(jobId)) {
            isPrivate = config.usePrivateChannels
        }

        return channel
            .broadcastFlow<WorkerLocationBroadcastPayload>(event = config.locationEvent)
            .map { payload ->
                SupabaseRealtimeMapper.toDomain(
                    jobId = jobId,
                    payload = payload,
                )
            }
            .onStart {
                if (config.usePrivateChannels) {
                    val accessToken = accessTokenProvider()
                        ?: error("Supabase access token is required for private tracking channels")
                    supabaseClient.realtime.setAuth(accessToken)
                    channel.updateAuth(accessToken)
                }
                supabaseClient.realtime.connect()
                channel.subscribe(blockUntilSubscribed = true)
            }
            .onCompletion {
                runCatching { channel.unsubscribe() }
                runCatching { supabaseClient.realtime.removeChannel(channel) }
            }
    }
}
