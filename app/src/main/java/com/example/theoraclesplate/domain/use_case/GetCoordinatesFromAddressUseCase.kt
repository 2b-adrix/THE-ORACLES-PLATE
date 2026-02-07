package com.example.theoraclesplate.domain.use_case

import com.example.theoraclesplate.domain.repository.GeocodingRepository
import org.osmdroid.util.GeoPoint

class GetCoordinatesFromAddressUseCase(private val repository: GeocodingRepository) {

    suspend operator fun invoke(address: String): GeoPoint? {
        return repository.getCoordinatesFromAddress(address)
    }
}
