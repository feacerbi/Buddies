package com.buddies.common.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationConverter(
    context: Context
) {

    private val coder = Geocoder(context)

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun geoPositionFromAddress(address: String) = withContext(Dispatchers.IO) {

        val location: Address? = try {
            coder.getFromLocationName(address,1)
        } catch (ioException: Exception) {
            emptyList()
        }.firstOrNull()

        val latitude = location?.latitude
        val longitude = location?.longitude

        latitude to longitude
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getLocationsFromAddress(address: String, maxResults: Int) = withContext(Dispatchers.IO) {
        try {
            coder.getFromLocationName(address, maxResults)
                .map { it.toFormattedString() }
        } catch (exception: Exception) {
            emptyList()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getAddressFromPosition(latitude: Double, longitude: Double) = withContext(Dispatchers.IO) {
        try {
            coder.getFromLocation(latitude, longitude, 1).firstOrNull()
                ?.toFormattedString() ?: ""
        } catch (exception: Exception) {
            ""
        }
    }

    private fun Address.toFormattedString() =
        thoroughfare + getStreetNumber(this) + ", " + adminArea + " - " + countryName

    private fun getStreetNumber(address: Address) =
        when (address.subThoroughfare) {
            null -> ""
            else -> " " + address.subThoroughfare
        }
}