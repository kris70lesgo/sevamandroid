package com.sevam.core.location

import com.sevam.core.common.model.SevamSampleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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
