package br.infnet.smpa_gabriel_justino_assessmnt.services

import android.Manifest
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
    fun getLastLocation(
        lm: LocationManager, listener: LocationListener?): Location? {
        val defaultListener = listener ?: object : LocationListener {

            override fun onLocationChanged(location: Location) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}


        }

        var adaptativeProvider = LocationManager.GPS_PROVIDER
        val isGpsTurnOn: Boolean = lm.isProviderEnabled(adaptativeProvider)
        val isAllowed = { permission: String ->
            ActivityCompat.checkSelfPermission(
                contxt,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }


        if (isAllowed(Manifest.permission.ACCESS_FINE_LOCATION)
            && isAllowed(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            var lastPlace:Location? = null
            if(isGpsTurnOn){
                lm.requestLocationUpdates(
                    adaptativeProvider, 2000L,
                    0f, defaultListener
                )
                lastPlace = lm.getLastKnownLocation(adaptativeProvider)

            }

            if (lastPlace == null) {
                adaptativeProvider = LocationManager.NETWORK_PROVIDER
                if (!lm.isProviderEnabled(adaptativeProvider)) {
                    return null
                }

                lm.requestLocationUpdates(adaptativeProvider, 2000L,
                    0f, defaultListener
                )
                lastPlace = lm.getLastKnownLocation(adaptativeProvider)
            }
            return lastPlace
        } else {
            return null
        }
    }
}
