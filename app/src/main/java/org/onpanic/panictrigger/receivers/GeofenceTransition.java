package org.onpanic.panictrigger.receivers;

import android.app.IntentService;
import android.content.Intent;

public class GeofenceTransition extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "org.onpanic.panictrigger.receivers.action.FOO";
    public static final String ACTION_BAZ = "org.onpanic.panictrigger.receivers.action.BAZ";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "org.onpanic.panictrigger.receivers.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "org.onpanic.panictrigger.receivers.extra.PARAM2";

    public GeofenceTransition() {
        super("GeofenceTransition");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
