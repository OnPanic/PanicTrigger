package org.onpanic.panictrigger;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.onpanic.panictrigger.activities.PanicActivity;
import org.onpanic.panictrigger.constants.PanicTriggerConstants;
import org.onpanic.panictrigger.fragments.ConfirmationsFragment;
import org.onpanic.panictrigger.fragments.DeadManFragmentStart;
import org.onpanic.panictrigger.fragments.DeadManFragmentStop;
import org.onpanic.panictrigger.fragments.LockedByPermissions;
import org.onpanic.panictrigger.fragments.NotificationsFragment;
import org.onpanic.panictrigger.fragments.PanicFragment;
import org.onpanic.panictrigger.fragments.PasswordFailFragment;
import org.onpanic.panictrigger.fragments.ReceiversFragment;
import org.onpanic.panictrigger.fragments.StartGeofencesFragment;
import org.onpanic.panictrigger.notifications.PanicNotification;
import org.onpanic.panictrigger.permissions.PermissionManager;
import org.onpanic.panictrigger.receivers.DeadManReceiver;
import org.onpanic.panictrigger.receivers.PasswordFailsReceiver;

import java.util.UUID;

import info.guardianproject.panic.Panic;
import info.guardianproject.panic.PanicTrigger;

public class PanicTriggerActivity extends AppCompatActivity implements
        ReceiversFragment.RequestConnection,
        PasswordFailFragment.RequestPermissions,
        ConfirmationsFragment.TestConfirmation,
        NotificationsFragment.PanicNotificationCallbacks,
        PanicFragment.OnPanicFragmentAction,
        NavigationView.OnNavigationItemSelectedListener,
        DeadManFragmentStart.DeadManStartCallBack,
        DeadManFragmentStop.DeadManStopCallBack,
        StartGeofencesFragment.OnGeofenceStart {

    private FragmentManager mFragmentManager;
    private String requestPackageName;
    private SharedPreferences prefs;
    private DrawerLayout drawer;
    private PasswordFailFragment passwordFailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panic_trigger_main_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Switch sw = new Switch(this);
        sw.setChecked(prefs.getBoolean(getString(R.string.pref_dry_run_enabled), false));
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                saveDryRunState(b);
            }
        });

        MenuItem dryRun = navigationView.getMenu().findItem(R.id.dry_run);
        dryRun.setActionView(sw);

        mFragmentManager = getFragmentManager();

        // Do not overlapping fragments.
        if (savedInstanceState == null) {
            if (prefs.getBoolean(getString(R.string.pref_deadman_running), false)) {
                DeadManFragmentStop stop = new DeadManFragmentStop();
                stop.setDeadTime(prefs.getLong(getString(R.string.pref_deadman_timestamp), 0));
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, stop)
                        .commit();
            } else {
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new PanicFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.trigger:
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new PanicFragment())
                        .commit();
                break;
            case R.id.dead_man:
                FragmentTransaction transaction = mFragmentManager.beginTransaction();

                if (prefs.getBoolean(getString(R.string.pref_deadman_running), false)) {
                    DeadManFragmentStop stop = new DeadManFragmentStop();
                    stop.setDeadTime(prefs.getLong(getString(R.string.pref_deadman_timestamp), 0));
                    transaction.replace(R.id.fragment_container, stop);
                } else {
                    transaction.replace(R.id.fragment_container, new DeadManFragmentStart());
                }

                transaction.commit();

                break;
            case R.id.unlock:
                passwordFailFragment = new PasswordFailFragment();
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, passwordFailFragment)
                        .commit();
                break;
            case R.id.geofences:
                if (PermissionManager.isLollipopOrHigher() && !PermissionManager.hasLocationPermission(this)) {
                    PermissionManager.requestLocationPermissions(this, PanicTriggerConstants.REQUEST_LOCATION_PERMISSION);
                } else {
                    mFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new StartGeofencesFragment())
                            .commit();
                }
                break;
            case R.id.notifications:
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new NotificationsFragment())
                        .commit();
                break;
            case R.id.confirmation:
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new ConfirmationsFragment())
                        .commit();
                break;
            case R.id.receivers:
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new ReceiversFragment())
                        .commit();
                break;
            case R.id.dry_run:
                Switch sw = (Switch) item.getActionView();
                sw.toggle();
                saveDryRunState(sw.isChecked());
                return true; // Do not close the drawer
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
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
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case PanicTriggerConstants.REQUEST_LOCATION_PERMISSION: {
                if (grantResults.length < 1
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    mFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new LockedByPermissions())
                            .commit();
                } else {
                    mFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new StartGeofencesFragment())
                            .commit();
                }

                break;
            }
        }
    }

    private void saveDryRunState(boolean state) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(getString(R.string.pref_dry_run_enabled), state);
        edit.apply();
    }

    /*
     * --- Fragments Callbacks ---
     */

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
        PanicNotification notification = new PanicNotification(this);
        notification.display(visible);
    }

    @Override
    public void runPanicTrigger() {
        Intent intent = new Intent(PanicTriggerActivity.this, PanicActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void connectToApp(String rowPackageName, boolean connect) {
        Intent intent;
        int action;

        requestPackageName = rowPackageName;

        if (connect) {
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
    public void deadManStart(Long time) {
        String rand = UUID.randomUUID().toString();

        SharedPreferences.Editor edit = prefs.edit();

        edit.putBoolean(getString(R.string.pref_deadman_running), true);
        edit.putLong(getString(R.string.pref_deadman_timestamp), time);
        edit.putString(getString(R.string.pref_deadman_rand), rand);
        edit.apply();

        Intent trigger = new Intent(this, DeadManReceiver.class);
        trigger.setAction(rand);

        PendingIntent panic = PendingIntent.getBroadcast(
                this,
                0,
                trigger,
                0
        );

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= 19) {
            manager.setExact(AlarmManager.RTC_WAKEUP, time, panic);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, time, panic);
        }

        DeadManFragmentStop stop = new DeadManFragmentStop();
        stop.setDeadTime(time);
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, stop)
                .commit();
    }

    @Override
    public void deadManStop() {
        Intent trigger = new Intent(this, DeadManReceiver.class);
        trigger.setAction(prefs.getString(getString(R.string.pref_deadman_rand), null));

        PendingIntent panic = PendingIntent.getBroadcast(
                this,
                0,
                trigger,
                0
        );

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(panic);

        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(getString(R.string.pref_deadman_running), false);
        edit.putLong(getString(R.string.pref_deadman_timestamp), 0);
        edit.putString(getString(R.string.pref_deadman_rand), null);
        edit.apply();

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new DeadManFragmentStart())
                .commit();
    }

    @Override
    public void geofenceStart(Integer distance) {

    }
}