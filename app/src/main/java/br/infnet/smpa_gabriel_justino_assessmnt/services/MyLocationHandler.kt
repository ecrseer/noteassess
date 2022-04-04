package br.infnet.smpa_gabriel_justino_assessmnt.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.view.View
import androidx.core.app.ActivityCompat

class MyLocationHandler(
    val contxt: Context
) {
    val defaultListener =  object : LocationListener {

        override fun onLocationChanged(location: Location) {}
        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}


    }
    @SuppressLint("MissingPermission")
    private fun getLocalByNetwork(lm:LocationManager, defaultListener:LocationListener): Location? {
        val provider = LocationManager.NETWORK_PROVIDER
        if (!lm.isProviderEnabled(provider)) {
            return null
        }

        lm.requestLocationUpdates(provider, 2000L,
            0f, defaultListener
        )
        return lm.getLastKnownLocation(provider)
    }
    fun getLastLocation(
        lm: LocationManager, listener: LocationListener?): Location? {

        val selectedListener = listener ?: defaultListener;

        val isAllowed = { permission: String ->
            ActivityCompat.checkSelfPermission(
                contxt,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        var adaptativeProvider = LocationManager.GPS_PROVIDER
        val isGpsTurnOn: Boolean = lm.isProviderEnabled(adaptativeProvider)
        var lastPlace:Location? = null

        if (isAllowed(Manifest.permission.ACCESS_FINE_LOCATION)
            && isAllowed(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {

            if(isGpsTurnOn){
                lm.requestLocationUpdates(
                    adaptativeProvider, 2000L,
                    0f, selectedListener
                )
                lastPlace = lm.getLastKnownLocation(adaptativeProvider)

            }

            if (lastPlace == null) {
                lastPlace = getLocalByNetwork(lm,selectedListener)
            }
        } else {
            lastPlace = getLocalByNetwork(lm,selectedListener)
        }
        return lastPlace
    }
}
