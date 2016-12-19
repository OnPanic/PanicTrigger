package llanes.ezquerro.juan.panictrigger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import llanes.ezquerro.juan.panictrigger.R;
import llanes.ezquerro.juan.panictrigger.notification.PanicNotification;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (prefs.getBoolean(context.getString(R.string.pref_notification_enabled), false)) {
                PanicNotification notification = new PanicNotification(context);
                notification.display(true);
            }
        }
    }
}
