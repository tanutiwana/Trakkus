//Amritpal Singh
package com.example.trakkus.Model;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.nfc.Tag;

import com.example.trakkus.Services.GeofenceBroadcastReceiver;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;


public class GeoFenceHelper extends ContextWrapper {

    private static final String TAG = "GeofenceHelper";
    PendingIntent pendingIntent;

    public GeoFenceHelper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeoFencingRequest(Geofence geofence) {
        // Initilize geofence Request
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

    }

    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionType) {
        // initilize GeoFence
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionType)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();


    }

    public PendingIntent getPendingIntent() {
        // initilize pending intent
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getActivity(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        return pendingIntent;
    }

    public String getErrorString(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "Geofence not Availabe";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "Geofence too many geofences";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "Geofence too many pending intents";

            }
        }
        return e.getLocalizedMessage();

    }
}
