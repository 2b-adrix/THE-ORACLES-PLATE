package com.example.theoraclesplate.domain.repository

import org.osmdroid.util.GeoPoint

interface GeocodingRepository {
    suspend fun getCoordinatesFromAddress(address: String): GeoPoint?
}
