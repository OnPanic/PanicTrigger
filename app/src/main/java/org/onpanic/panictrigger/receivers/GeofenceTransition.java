package org.onpanic.panictrigger.receivers;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.onpanic.panictrigger.activities.PanicActivity;

public class GeofenceTransition extends IntentService {

    public GeofenceTransition(String name) {
        super(name);
    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_EXIT) {
            startActivity(new Intent(getApplicationContext(), PanicActivity.class));
        }
    }
}
