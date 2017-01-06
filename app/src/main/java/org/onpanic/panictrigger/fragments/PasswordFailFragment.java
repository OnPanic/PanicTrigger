package org.onpanic.panictrigger.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import org.onpanic.panictrigger.R;
import org.onpanic.panictrigger.interfaces.RequestPermissions;


public class PasswordFailFragment extends PreferenceFragment {
    private RequestPermissions requestPermissions;
    private SwitchPreference enable;

    public PasswordFailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.password_preferences);

        enable = (SwitchPreference) findPreference(getString(R.string.pref_login_action));
        enable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if ((boolean) o) {
                    requestPermissions.requestAdmin();
                }
                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RequestPermissions) {
            requestPermissions = (RequestPermissions) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RequestPermissions");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        requestPermissions = null;
    }

    public void adminDenied() {
        enable.setChecked(false);
    }
}
