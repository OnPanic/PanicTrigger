package org.onpanic.panictrigger;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import org.onpanic.panictrigger.activities.PanicActivity;
import org.onpanic.panictrigger.activities.ReceiversActivity;
import org.onpanic.panictrigger.constants.PanicTriggerConstants;
import org.onpanic.panictrigger.delegate.AppCompatPreferenceActivity;
import org.onpanic.panictrigger.notification.PanicNotification;
import org.onpanic.panictrigger.receivers.PasswordFailsReceiver;

public class PanicTriggerActivity extends AppCompatPreferenceActivity {
    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener mSettingsObserver;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName deviceAdminComponentName;
    private Preference runTest;
    private Preference showReceivers;
    private SwitchPreference swipeDialog;
    private SwitchPreference countdownDialog;
    private SwitchPreference loginAction;
    private PanicNotification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        notification = new PanicNotification(this);
        notification.display(
                (prefs.getBoolean(getString(R.string.pref_notification_enabled), true) && !notification.isVisible())
        );

        setLayoutActions();

        setPreferencesListener();
    }

    private void setLayoutActions() {
        runTest = (Preference) findPreference(getString(R.string.pref_run_test));
        runTest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent intent = new Intent(PanicTriggerActivity.this, PanicActivity.class);
                intent.putExtra(PanicTriggerConstants.TEST_RUN, true);
                startActivity(intent);
                finish();
                return false;
            }
        });

        loginAction = (SwitchPreference) findPreference(getString(R.string.pref_login_action));
        swipeDialog = (SwitchPreference) findPreference(getString(R.string.pref_dialog_swipe));
        countdownDialog = (SwitchPreference) findPreference(getString(R.string.pref_countdown_enabled));

        showReceivers = (Preference) findPreference(getString(R.string.pref_app_listeners));
        showReceivers.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent intent = new Intent(PanicTriggerActivity.this, ReceiversActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }

    private void setPreferencesListener() {
        mSettingsObserver = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(getString(R.string.pref_login_action))) {
                    requestAdminPermissions();
                } else if (key.equals(getString(R.string.pref_dialog_swipe))) {
                    countdownDialog.setChecked(!sharedPreferences.getBoolean(key, false));
                } else if (key.equals(getString(R.string.pref_countdown_enabled))) {
                    swipeDialog.setChecked(!sharedPreferences.getBoolean(key, false));
                } else if (key.equals(getString(R.string.pref_notification_enabled))) {
                    notification.display(
                            sharedPreferences.getBoolean(key, true));
                }
            }
        };

        prefs.registerOnSharedPreferenceChangeListener(mSettingsObserver);
    }

    private void requestAdminPermissions() {
        devicePolicyManager
                = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdminComponentName
                = new ComponentName(PanicTriggerActivity.this, PasswordFailsReceiver.class);

        if (!devicePolicyManager.isAdminActive(deviceAdminComponentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.monitor_login_failues));
            startActivityForResult(intent, PanicTriggerConstants.DEVICE_ADMIN_ACTIVATION_REQUEST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(mSettingsObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(mSettingsObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.unregisterOnSharedPreferenceChangeListener(mSettingsObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        prefs.unregisterOnSharedPreferenceChangeListener(mSettingsObserver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PanicTriggerConstants.DEVICE_ADMIN_ACTIVATION_REQUEST:
                if (!devicePolicyManager.isAdminActive(deviceAdminComponentName)) {
                    loginAction.setChecked(false);
                }
                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
