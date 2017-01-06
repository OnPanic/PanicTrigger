package org.onpanic.panictrigger.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import org.onpanic.panictrigger.R;

public class NotificationsFragment extends PreferenceFragment {
    private PanicNotificationCallbacks panicNotificationCallbacks;
    private SwitchPreference panicNotificacion;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notifications_preferences);

        panicNotificacion = (SwitchPreference) findPreference(getString(R.string.pref_notification_enabled));
        panicNotificacion.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                panicNotificationCallbacks.visible((boolean) o);
                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PanicNotificationCallbacks) {
            panicNotificationCallbacks = (PanicNotificationCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PanicNotificationCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        panicNotificationCallbacks = null;
    }

    public interface PanicNotificationCallbacks {
        void visible(Boolean visible);
    }
}
