package org.onpanic.panictrigger.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.onpanic.panictrigger.R;
import org.onpanic.panictrigger.interfaces.FragmentSwitch;


public class PreferencesFragment extends PreferenceFragment {
    private FragmentSwitch fragmentSwitch;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_preferences);

        Preference passwords = findPreference(getString(R.string.pref_password_fails));
        passwords.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                fragmentSwitch.loadFragment(R.xml.password_preferences);
                return true;
            }
        });

        Preference notifications = findPreference(getString(R.string.pref_notifications));
        notifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                fragmentSwitch.loadFragment(R.xml.notifications_preferences);
                return true;
            }
        });

        Preference confirmations = findPreference(getString(R.string.pref_confirmations));
        confirmations.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                fragmentSwitch.loadFragment(R.xml.confirmation_preferences);
                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentSwitch) {
            fragmentSwitch = (FragmentSwitch) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentSwitch");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentSwitch = null;
    }
}
