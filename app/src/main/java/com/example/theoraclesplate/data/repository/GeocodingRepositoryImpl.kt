package com.example.theoraclesplate.data.repository

import android.content.Context
import android.location.Geocoder
import com.example.theoraclesplate.domain.repository.GeocodingRepository
import org.osmdroid.util.GeoPoint
import java.io.IOException

class GeocodingRepositoryImpl(private val context: Context) : GeocodingRepository {

    override suspend fun getCoordinatesFromAddress(address: String): GeoPoint? {
        val geocoder = Geocoder(context)
        return try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val location = addresses[0]
                GeoPoint(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: IOException) {
            null
        }
    }
}
