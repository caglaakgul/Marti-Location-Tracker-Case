package com.caglaakgul.martilocationtrackercase.presentation.tracking.mapper

import com.caglaakgul.martilocationtrackercase.domain.model.RoutePoint
import com.caglaakgul.martilocationtrackercase.domain.model.UserLocation
import com.caglaakgul.martilocationtrackercase.presentation.tracking.TrackingUiState
import com.google.android.gms.maps.model.LatLng

fun defaultTrackingTexts(): TrackingUiState.Texts {
    return TrackingUiState.Texts(
        addressTitle = "Adres",
        trackingActive = "Takip aktif",
        trackingStopped = "Takip durdu",
        routePointCountSuffix = "konum noktası",
        startButton = "Başlat",
        stopButton = "Durdur",
        resetButton = "Sıfırla",
        markerTitlePrefix = "Konum",
        addressNotFound = "Adres bulunamadı",
        noInternetConnection = "İnternet bağlantınızı kontrol edin"
    )
}

fun UserLocation.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun RoutePoint.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun RoutePoint.toUserLocation(fallbackRecordedAt: Long): UserLocation {
    return UserLocation(
        latitude = latitude,
        longitude = longitude,
        recordedAt = fallbackRecordedAt
    )
}

fun RoutePoint.toUserLocation(): UserLocation {
    return UserLocation(
        latitude = latitude,
        longitude = longitude,
        recordedAt = createdAt
    )
}
