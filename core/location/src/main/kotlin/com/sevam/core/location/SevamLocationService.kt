package com.sevam.core.location

import com.sevam.core.common.model.SevamSampleData
import com.sevam.core.network.SevamApiConfig
import com.sevam.core.network.SevamApiService
import com.sevam.core.network.executeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json

data class DeviceLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float? = null,
)

data class LocationSuggestion(
    val id: String,
    val title: String,
    val subtitle: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

data class ReverseGeocodeResult(
    val shortLabel: String,
    val fullAddress: String,
)

interface LocationSearchClient {
    fun search(query: String): Flow<List<LocationSuggestion>>
    fun reverseGeocode(latitude: Double, longitude: Double): Flow<ReverseGeocodeResult>
    fun currentLocationHint(): Flow<DeviceLocation?>
}

class MockLocationSearchClient : LocationSearchClient {
    override fun search(query: String): Flow<List<LocationSuggestion>> {
        val suggestions = SevamSampleData.addresses
            .filter {
                query.isBlank() ||
                    it.label.contains(query, ignoreCase = true) ||
                    it.line1.contains(query, ignoreCase = true) ||
                    it.line2.contains(query, ignoreCase = true)
            }
            .map {
                LocationSuggestion(
                    id = it.id,
                    title = it.label,
                    subtitle = "${it.line1}, ${it.line2}, ${it.city}",
                )
            }
        return flowOf(suggestions)
    }

    override fun reverseGeocode(latitude: Double, longitude: Double): Flow<ReverseGeocodeResult> {
        return flowOf(
            ReverseGeocodeResult(
                shortLabel = "Current Location",
                fullAddress = "Mapbox reverse geocode pending for $latitude, $longitude",
            ),
        )
    }

    override fun currentLocationHint(): Flow<DeviceLocation?> {
        return flowOf(
            DeviceLocation(
                latitude = 12.9352,
                longitude = 77.6245,
                accuracyMeters = 38f,
            ),
        )
    }
}

class RemoteLocationSearchClient(
    private val apiConfig: SevamApiConfig,
    private val apiService: SevamApiService,
    private val json: Json,
) : LocationSearchClient {
    override fun search(query: String): Flow<List<LocationSuggestion>> = flow {
        if (!apiConfig.isConfigured || query.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val result = executeApiCall(json) { apiService.searchLocations(query) }
        emit(
            result.getOrDefault(emptyList()).mapIndexed { index, suggestion ->
                LocationSuggestion(
                    id = "remote-$index",
                    title = suggestion.name.substringBefore(",").ifBlank { suggestion.name },
                    subtitle = suggestion.name,
                    latitude = suggestion.lat,
                    longitude = suggestion.lng,
                )
            },
        )
    }

    override fun reverseGeocode(latitude: Double, longitude: Double): Flow<ReverseGeocodeResult> = flow {
        if (!apiConfig.isConfigured) {
            emit(
                ReverseGeocodeResult(
                    shortLabel = "Current Location",
                    fullAddress = "$latitude, $longitude",
                ),
            )
            return@flow
        }
        val result = executeApiCall(json) { apiService.reverseGeocode(latitude, longitude) }
        val reverse = result.getOrNull()
        emit(
            ReverseGeocodeResult(
                shortLabel = reverse?.name?.substringBefore(",") ?: "Current Location",
                fullAddress = reverse?.name ?: "$latitude, $longitude",
            ),
        )
    }

    override fun currentLocationHint(): Flow<DeviceLocation?> {
        return flowOf(
            DeviceLocation(
                latitude = 12.9352,
                longitude = 77.6245,
                accuracyMeters = 38f,
            ),
        )
    }
}
