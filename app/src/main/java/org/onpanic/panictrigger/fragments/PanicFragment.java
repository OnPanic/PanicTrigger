package org.onpanic.panictrigger.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.onpanic.panictrigger.R;

public class PanicFragment extends Fragment {

    private OnPanicFragmentAction mListener;

    public PanicFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_panic, container, false);
        ImageView panic = (ImageView) v.findViewById(R.id.main_trigger);
        panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.runPanicTrigger();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPanicFragmentAction) {
            mListener = (OnPanicFragmentAction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPanicFragmentAction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPanicFragmentAction {
        void runPanicTrigger();
    }
}