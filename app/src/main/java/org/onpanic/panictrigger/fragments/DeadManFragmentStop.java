package org.onpanic.panictrigger.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.onpanic.panictrigger.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DeadManFragmentStop extends Fragment {
    private TextView date;

    private DeadManStopCallBack deadManStopCallBack;

    public DeadManFragmentStop() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dead_man_stop, container, false);

        date = (TextView) v.findViewById(R.id.deadman_date);

        ImageButton button = (ImageButton) v.findViewById(R.id.stop_deadman);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deadManStopCallBack.deadManStop();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DeadManStopCallBack) {
            deadManStopCallBack = (DeadManStopCallBack) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DeadManStopCallBack");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        deadManStopCallBack = null;
    }

    public void setDeadDate(Long time) {
        Date timestamp = new Date(time);
        SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat day = new SimpleDateFormat("EEE", Locale.getDefault());
        date.setText(hour.format(timestamp) + " " + day.format(timestamp));
    }

    public interface DeadManStopCallBack {
        void deadManStop();
    }
}
