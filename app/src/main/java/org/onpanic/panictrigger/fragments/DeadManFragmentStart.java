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

public class DeadManFragmentStart extends Fragment {

    private final Integer[] units = {60, 3600, 24800};

    private Spinner mDeadSpinner;
    private Context mContext;
    private EditText mDeadAmountText;
    private DeadManStartCallBack deadManStartCallBack;

    public DeadManFragmentStart() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dead_man_start, container, false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.time_unit, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mDeadSpinner = (Spinner) v.findViewById(R.id.dead_time_spinner);
        mDeadSpinner.setAdapter(adapter);
        mDeadSpinner.setSelection(1);

        mDeadAmountText = (EditText) v.findViewById(R.id.dead_time_amount);
        mDeadAmountText.setText("1");

        ImageButton button = (ImageButton) v.findViewById(R.id.run_deadman);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer amount = Integer.parseInt(mDeadAmountText.getText().toString());
                Integer unit = units[mDeadSpinner.getSelectedItemPosition()];
                deadManStartCallBack.deadManStart((amount * unit * 1000L) + System.currentTimeMillis());
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof DeadManStartCallBack) {
            deadManStartCallBack = (DeadManStartCallBack) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DeadManStartCallBack");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        deadManStartCallBack = null;
    }

    public interface DeadManStartCallBack {
        void deadManStart(Long time);
    }
}
