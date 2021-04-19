package com.buddies.common.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationConverter(
    private val context: Context
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun geoPositionFromAddress(address: String) = withContext(Dispatchers.IO) {
        val coder = Geocoder(context)

        val location: Address? = try {
            coder.getFromLocationName(address,1)
        } catch (ioException: Exception) {
            emptyList()
        }.firstOrNull()

        val latitude = location?.latitude
        val longitude = location?.longitude

        latitude to longitude
    }

}