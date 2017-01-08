package org.onpanic.panictrigger.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import org.onpanic.panictrigger.R;
import org.onpanic.panictrigger.constants.PanicTriggerConstants;
import org.onpanic.panictrigger.notifications.TriggerNotification;

import info.guardianproject.panic.PanicTrigger;

public class PanicActivity extends Activity {
    private boolean mTestRun = false;
    private boolean mNotify = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent confirmationMethod;

        Intent request = getIntent();

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(PanicActivity.this);

        mNotify = mPrefs.getBoolean(getString(R.string.pref_runned_notification), true);

        mTestRun = (request.getBooleanExtra(PanicTriggerConstants.TEST_RUN, false)
                || mPrefs.getBoolean(getString(R.string.pref_dry_run_enabled), false));

        boolean doNotConfirm = (request.getBooleanExtra(PanicTriggerConstants.RUN_FROM_LOGIN, false)
                && mPrefs.getBoolean(getString(R.string.pref_disable_dialog_on_login), false))
                || request.getBooleanExtra(PanicTriggerConstants.RUN_DEAD_MAN, false);

        if (mPrefs.getBoolean(getString(R.string.pref_dialog_none), false) || doNotConfirm) {
            runTrigger();
        } else if (mPrefs.getBoolean(getString(R.string.pref_dialog_swipe), true)) {
            confirmationMethod = new Intent(this, SwipeActivity.class);
            startActivityForResult(confirmationMethod, PanicTriggerConstants.SWIPE_CONFIRMATION);
        } else if (mPrefs.getBoolean(getString(R.string.pref_countdown_enabled), false)) {
            confirmationMethod = new Intent(this, CountDownActivity.class);
            startActivityForResult(confirmationMethod, PanicTriggerConstants.COUNTDOWN_CONFIRMATION);
        }
    }

    private void runTrigger() {
        if (mNotify) {
            TriggerNotification notification = new TriggerNotification(PanicActivity.this);
            notification.show(mTestRun);
        }

        if (mTestRun) {
            if (Build.VERSION.SDK_INT >= 21) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        } else {
            PanicTrigger.sendTrigger(PanicActivity.this);

            final Activity activity = PanicActivity.this;

            /* This app needs to stay running for a while to make sure that it sends
             * all of the Intents to Activities, Services, and BroadcastReceivers. If
             * it exits too soon, they will not get sent. */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ExitActivity.exitAndRemoveFromRecentApps(PanicActivity.this);

                    if (Build.VERSION.SDK_INT >= 21) {
                        activity.finishAndRemoveTask();
                    } else {
                        activity.finish();
                    }
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
            if (Build.VERSION.SDK_INT >= 21) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        }
    }
}
