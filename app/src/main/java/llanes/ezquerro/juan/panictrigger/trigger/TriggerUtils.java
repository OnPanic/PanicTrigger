package llanes.ezquerro.juan.panictrigger.trigger;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import info.guardianproject.panic.PanicTrigger;
import llanes.ezquerro.juan.panictrigger.R;
import llanes.ezquerro.juan.panictrigger.activities.ExitActivity;

public class TriggerUtils {
    public static void send(Activity activity) {
        final Activity mActivity = activity;
        PanicTrigger.sendTrigger(mActivity);
        Toast.makeText(mActivity, R.string.done, Toast.LENGTH_LONG).show();

                            /* This app needs to stay running for a while to make sure that it sends
                             * all of the Intents to Activities, Services, and BroadcastReceivers. If
                             * it exits too soon, they will not get sent. */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ExitActivity.exitAndRemoveFromRecentApps(mActivity);
            }
        }, 10000); // 10 second delay
    }
}
