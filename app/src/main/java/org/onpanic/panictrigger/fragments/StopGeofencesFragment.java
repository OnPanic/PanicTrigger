package org.onpanic.panictrigger.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.onpanic.panictrigger.R;

public class StopGeofencesFragment extends Fragment {
    private OnGeofenceStop onGeofenceStop;

    public StopGeofencesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_geofence_stop, container, false);

        ImageButton button = (ImageButton) v.findViewById(R.id.stop_geofence);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGeofenceStop.geofenceStop();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGeofenceStop) {
            onGeofenceStop = (OnGeofenceStop) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGeofenceStop");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onGeofenceStop = null;
    }

    public interface OnGeofenceStop {
        void geofenceStop();
    }
}
