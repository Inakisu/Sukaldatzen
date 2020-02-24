package com.stirling.developments.Utils;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Responsible for handle the location service provider.
 * Created by cristianoliveira on 25/05/15.
 */
public class LocationTracker {

    private LocationManager locationManager;

    public LocationTracker(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    /**
     * Verify if GPS_PROVIDER is Available / Enabled
     *
     * @return boolean yes / no
     */
    public boolean isGpsLocationServiceEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Verificar si NETWORK_PROVIDER está Available / Enabled
     *
     * @return boolean yes / no
     */
    public boolean isNetworkLocationServiceEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Verificar que haya por lo menos un proveedor Available / Enabled
     *
     * @return boolean yes / no
     */
    public boolean isLocationServiceEnabled() {
        return isGpsLocationServiceEnabled() || isNetworkLocationServiceEnabled();
    }

    /**
     * Registrar un LocationListener al LocationService.
     *
     * @param locationLinstener interface
     * @return boolean Listener ha sido registrado?
     */
    public boolean startListener(LocationListener locationLinstener) {

        boolean isGpsEnabled = isLocationServiceEnabled();

        if (isGpsLocationServiceEnabled()) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                    , LocationConstants.TIME_SECONDS_REFRESH
                    , LocationConstants.MIN_DISTANCE_REFRESH
                    , locationLinstener);

            Location lastLocation =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            locationLinstener.onLocationChanged(lastLocation);

            return true;

        } else if (isLocationServiceEnabled()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                    , LocationConstants.TIME_SECONDS_REFRESH
                    , LocationConstants.MIN_DISTANCE_REFRESH
                    , locationLinstener);

            Location lastLocation =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            locationLinstener.onLocationChanged(lastLocation);

            return true;
        }

        return false;
    }

    /**
     * Return last cached Location from enabled providers
     *
     * @return Cached Location if it exists else return null.
     */
    public Location getLastKnowLocation() {

        for (String providerName : locationManager.getProviders(true)) {
            Location location = locationManager.getLastKnownLocation(providerName);
            if (location !=  null) {
                return location;
            }
        }

        return null;
    }
}