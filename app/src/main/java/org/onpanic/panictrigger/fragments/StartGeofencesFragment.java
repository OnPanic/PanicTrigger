package org.onpanic.panictrigger.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import org.onpanic.panictrigger.R;


public class StartGeofencesFragment extends Fragment {
    private final Integer[] units = {1, 1000};

    private Spinner distanceUnit;
    private Context mContext;
    private EditText distance;
    private OnGeofenceStart mCallback;

    public StartGeofencesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.start_geofences_fragment, container, false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.distance_unit, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        distanceUnit = (Spinner) v.findViewById(R.id.geofences_spinner);
        distanceUnit.setAdapter(adapter);
        distanceUnit.setSelection(0);

        distance = (EditText) v.findViewById(R.id.geofence_distance);
        distance.setText("200");

        ImageButton button = (ImageButton) v.findViewById(R.id.run_geofences);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer amount = Integer.parseInt(distance.getText().toString());
                Integer unit = units[distanceUnit.getSelectedItemPosition()];
                mCallback.geofenceStart(unit * amount);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnGeofenceStart) {
            mCallback = (OnGeofenceStart) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGeofenceStart");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface OnGeofenceStart {
        void geofenceStart(Integer distance);
    }

}
