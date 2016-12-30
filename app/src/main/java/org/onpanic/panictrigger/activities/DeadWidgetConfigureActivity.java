package org.onpanic.panictrigger.activities;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.onpanic.panictrigger.R;
import org.onpanic.panictrigger.ui.DeadWidget;

public class DeadWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "org.onpanic.panictrigger.ui.DeadWidget";
    private static final String PREF_PREFIX_KEY = "deadwidget_";
    private final Integer[] units = {60, 3600, 24800};
    private int mDeadWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText mDeadAmountText;
    private Spinner mDeadSpinner;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = DeadWidgetConfigureActivity.this;

            Integer amount = Integer.parseInt(mDeadAmountText.getText().toString());
            Integer unit = mDeadSpinner.getSelectedItemPosition();
            saveDeadTimePref(context, mDeadWidgetId, amount, units[unit]);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            DeadWidget.updateAppWidget(context, appWidgetManager, mDeadWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mDeadWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public DeadWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveDeadTimePref(Context context, int appWidgetId, Integer amount, Integer unit) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, unit * amount);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static Integer loadDeadTimePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 3600);

    }

    public static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] ids = appWidgetManager.getAppWidgetIds(
                new ComponentName(getApplicationContext(), DeadWidget.class));
        if (ids.length > 1) finish();

        setContentView(R.layout.dead_widget_configure);
        mDeadAmountText = (EditText) findViewById(R.id.dead_man_amount);
        mDeadSpinner = (Spinner) findViewById(R.id.dead_man_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.time_unit, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDeadSpinner.setAdapter(adapter);

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mDeadWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mDeadWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mDeadAmountText.setText("1");
        mDeadSpinner.setSelection(1);
    }
}

