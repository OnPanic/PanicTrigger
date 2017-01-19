package org.onpanic.panictrigger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.onpanic.panictrigger.R;
import org.onpanic.panictrigger.activities.PanicActivity;
import org.onpanic.panictrigger.constants.PanicTriggerConstants;

import java.util.UUID;

public class DeadManReceiver extends BroadcastReceiver {
    public DeadManReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Boolean runFromDeadManTrigger = (prefs.getBoolean(context.getString(R.string.pref_deadman_running), false)) &&
                (action.equals(prefs.getString(context.getString(R.string.pref_deadman_rand), UUID.randomUUID().toString())));

        if (runFromDeadManTrigger) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(context.getString(R.string.pref_deadman_running), false);
            edit.putLong(context.getString(R.string.pref_deadman_timestamp), 0);
            edit.putString(context.getString(R.string.pref_deadman_rand), UUID.randomUUID().toString());
            edit.apply();

            Intent panic = new Intent(context, PanicActivity.class);
            panic.putExtra(PanicTriggerConstants.RUN_DEAD_MAN, true);
            context.startActivity(panic);
        }
    }
}
