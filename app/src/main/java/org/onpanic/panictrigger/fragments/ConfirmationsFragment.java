package org.onpanic.panictrigger.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import org.onpanic.panictrigger.R;
import org.onpanic.panictrigger.interfaces.TestConfirmation;


public class ConfirmationsFragment extends PreferenceFragment {
    private SwitchPreference swipe;
    private SwitchPreference countdown;
    private Preference runTest;
    private TestConfirmation testConfirmation;

    public ConfirmationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.confirmation_preferences);

        swipe = (SwitchPreference) findPreference(getString(R.string.pref_dialog_swipe));
        countdown = (SwitchPreference) findPreference(getString(R.string.pref_countdown_enabled));
        runTest = (Preference) findPreference(getString(R.string.pref_run_test));

        swipe.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                countdown.setChecked(!((boolean) o));
                return true;
            }
        });

        countdown.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                swipe.setChecked(!((boolean) o));
                return true;
            }
        });

        runTest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                testConfirmation.runTest();
                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TestConfirmation) {
            testConfirmation = (TestConfirmation) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TestConfirmation");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        testConfirmation = null;
    }
}
