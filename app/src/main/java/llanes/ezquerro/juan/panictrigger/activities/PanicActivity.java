package llanes.ezquerro.juan.panictrigger.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import info.guardianproject.panic.PanicTrigger;
import llanes.ezquerro.juan.panictrigger.R;
import llanes.ezquerro.juan.panictrigger.constants.PanicTriggerConstants;
import llanes.ezquerro.juan.panictrigger.notification.TriggerNotification;

public class PanicActivity extends Activity {
    private boolean mTestRun = false;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent confirmationMethod;

        Intent request = getIntent();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mTestRun = (request.getBooleanExtra(PanicTriggerConstants.TEST_RUN, false)
                || prefs.getBoolean(getString(R.string.pref_dry_run_enabled), false));

        boolean doNotConfirmAfterPasswordFail = (request.getBooleanExtra(PanicTriggerConstants.RUN_FROM_LOGIN, false)
                && prefs.getBoolean(getString(R.string.pref_disable_dialog_on_login), false));

        if (prefs.getBoolean(getString(R.string.pref_dialog_none), false) || doNotConfirmAfterPasswordFail) {
            runTrigger();
        } else if (prefs.getBoolean(getString(R.string.pref_dialog_swipe), false)) {
            confirmationMethod = new Intent(this, SwipeActivity.class);
            startActivityForResult(confirmationMethod, PanicTriggerConstants.SWIPE_CONFIRMATION);
        } else if (prefs.getBoolean(getString(R.string.pref_countdown_enabled), false)) {
            confirmationMethod = new Intent(this, CountDownActivity.class);
            startActivityForResult(confirmationMethod, PanicTriggerConstants.COUNTDOWN_CONFIRMATION);
        }
    }

    private void runTrigger() {
        if (prefs.getBoolean(getString(R.string.pref_runned_notification), true)) {
            TriggerNotification notification = new TriggerNotification(getApplicationContext());
            notification.show(mTestRun);
        }

        if (mTestRun) {
            ExitActivity.exitAndRemoveFromRecentApps(PanicActivity.this);
        } else {
            PanicTrigger.sendTrigger(PanicActivity.this);

        /* This app needs to stay running for a while to make sure that it sends
         * all of the Intents to Activities, Services, and BroadcastReceivers. If
         * it exits too soon, they will not get sent. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ExitActivity.exitAndRemoveFromRecentApps(PanicActivity.this);
                }
            }, 10000); // 10 second delay
        }
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if (response == Activity.RESULT_OK) {
            runTrigger();
        } else {
            ExitActivity.exitAndRemoveFromRecentApps(PanicActivity.this);
        }
    }
}
