package org.onpanic.panictrigger;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.onpanic.panictrigger.activities.PanicActivity;
import org.onpanic.panictrigger.constants.PanicTriggerConstants;
import org.onpanic.panictrigger.fragments.ConfirmationsFragment;
import org.onpanic.panictrigger.fragments.NotificationsFragment;
import org.onpanic.panictrigger.fragments.PanicFragment;
import org.onpanic.panictrigger.fragments.PasswordFailFragment;
import org.onpanic.panictrigger.fragments.ReceiversFragment;
import org.onpanic.panictrigger.notifications.PanicNotification;
import org.onpanic.panictrigger.receivers.PasswordFailsReceiver;

import info.guardianproject.panic.Panic;
import info.guardianproject.panic.PanicTrigger;

public class PanicTriggerActivity extends AppCompatActivity implements
        ReceiversFragment.RequestConnection,
        PasswordFailFragment.RequestPermissions,
        ConfirmationsFragment.TestConfirmation,
        NotificationsFragment.PanicNotificationCallbacks,
        PanicFragment.OnPanicFragmentAction,
        NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager mFragmentManager;
    private String requestPackageName;

    private PasswordFailFragment passwordFailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panic_trigger_main_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFragmentManager = getFragmentManager();

        // Do not overlapping fragments.
        if (savedInstanceState != null) return;

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new PanicFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        switch (id) {
            case R.id.trigger:
                transaction.replace(R.id.fragment_container, new PanicFragment());
                break;
            case R.id.unlock:
                passwordFailFragment = new PasswordFailFragment();
                transaction.replace(R.id.fragment_container, passwordFailFragment);
                break;
            case R.id.notifications:
                transaction.replace(R.id.fragment_container, new NotificationsFragment());
                break;
            case R.id.confirmation:
                transaction.replace(R.id.fragment_container, new ConfirmationsFragment());
                break;
            case R.id.receivers:
                transaction.replace(R.id.fragment_container, new ReceiversFragment());
                break;
        }

        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
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
        PanicNotification notification = new PanicNotification(this);
        notification.display(visible);
    }

    @Override
    public void runPanicTrigger(Uri uri) {

    }
}
