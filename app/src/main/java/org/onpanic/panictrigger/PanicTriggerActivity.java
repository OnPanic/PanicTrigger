package org.onpanic.panictrigger;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.onpanic.panictrigger.activities.PanicActivity;
import org.onpanic.panictrigger.constants.PanicTriggerConstants;
import org.onpanic.panictrigger.fragments.ConfirmationsFragment;
import org.onpanic.panictrigger.fragments.NotificationsFragment;
import org.onpanic.panictrigger.fragments.PasswordFailFragment;
import org.onpanic.panictrigger.fragments.PreferencesFragment;
import org.onpanic.panictrigger.fragments.ReceiversFragment;
import org.onpanic.panictrigger.interfaces.FragmentSwitch;
import org.onpanic.panictrigger.interfaces.PanicNotificationCallbacks;
import org.onpanic.panictrigger.interfaces.RequestConnection;
import org.onpanic.panictrigger.interfaces.RequestPermissions;
import org.onpanic.panictrigger.interfaces.TestConfirmation;
import org.onpanic.panictrigger.notifications.PanicNotification;
import org.onpanic.panictrigger.receivers.PasswordFailsReceiver;

import info.guardianproject.panic.Panic;
import info.guardianproject.panic.PanicTrigger;

public class PanicTriggerActivity extends AppCompatActivity implements
        FragmentSwitch,
        RequestConnection,
        RequestPermissions,
        TestConfirmation,
        PanicNotificationCallbacks {

    private FragmentManager mFragmentManager;
    private String requestPackageName;

    private PasswordFailFragment passwordFailFragment;

    private PanicNotification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_trigger);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getFragmentManager();

        // Do not overlapping fragments.
        if (savedInstanceState != null) return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        notification = new PanicNotification(this);
        notification.display((prefs.getBoolean(getString(R.string.pref_notification_enabled), false)
                && !notification.isVisible())
        );

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new ReceiversFragment())
                .commit();
    }

    @Override
    public void loadFragment(int id) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        switch (id) {
            case R.xml.password_preferences:
                passwordFailFragment = new PasswordFailFragment();
                transaction.replace(R.id.fragment_container, passwordFailFragment);
                break;
            case R.xml.notifications_preferences:
                transaction.replace(R.id.fragment_container, new NotificationsFragment());
                break;
            case R.xml.confirmation_preferences:
                transaction.replace(R.id.fragment_container, new ConfirmationsFragment());
                break;
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            mFragmentManager.popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.panic_trigger_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            mFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, new PreferencesFragment())
                    .commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void connectToApp(String rowPackageName, boolean connected) {
        Intent intent;
        int action;

        requestPackageName = rowPackageName;

        if (connected) {
            intent = new Intent(Panic.ACTION_CONNECT);
            action = PanicTriggerConstants.CONNECT_RESULT;
        } else {
            intent = new Intent(Panic.ACTION_DISCONNECT);
            action = PanicTriggerConstants.DISCONNECT_RESULT;
        }

        intent.setPackage(requestPackageName);

        // TODO add TrustedIntents here
        startActivityForResult(intent, action);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PanicTriggerConstants.DEVICE_ADMIN_ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_CANCELED) {
                    passwordFailFragment.adminDenied();
                }
                return;
            case PanicTriggerConstants.CONNECT_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    PanicTrigger.addConnectedResponder(this, requestPackageName);
                }
                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void requestAdmin() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName deviceAdminComponentName = new ComponentName(PanicTriggerActivity.this, PasswordFailsReceiver.class);

        if (!devicePolicyManager.isAdminActive(deviceAdminComponentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.monitor_login_failues));
            startActivityForResult(intent, PanicTriggerConstants.DEVICE_ADMIN_ACTIVATION_REQUEST);
        }
    }

    @Override
    public void runTest() {
        Intent intent = new Intent(PanicTriggerActivity.this, PanicActivity.class);
        intent.putExtra(PanicTriggerConstants.TEST_RUN, true);
        startActivity(intent);
        finish();
    }

    @Override
    public void visible(Boolean visible) {
        notification.display(visible);
    }
}
