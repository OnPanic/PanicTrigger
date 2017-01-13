package org.onpanic.panictrigger.permissions;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import org.onpanic.panictrigger.R;

public class PermissionManager {

    public static boolean isLollipopOrHigher() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    @SuppressLint("NewApi")
    public static boolean hasLocationPermission(Context context) {
        return (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestLocationPermissions(FragmentActivity activity, int action) {
        final int mAction = action;
        final FragmentActivity mActivity = activity;

        if (ActivityCompat.shouldShowRequestPermissionRationale
                (mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(mActivity.findViewById(android.R.id.content),
                    R.string.please_grant_permissions_for_get_location,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.activate,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(mActivity,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    mAction);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    mAction);
        }
    }
}

