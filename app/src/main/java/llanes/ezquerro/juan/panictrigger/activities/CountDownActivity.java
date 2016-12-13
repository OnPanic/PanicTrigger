package llanes.ezquerro.juan.panictrigger.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import llanes.ezquerro.juan.panictrigger.R;

public class CountDownActivity extends Activity {

    private static final String KEY_COUNT_DOWN_DONE = "keyCountDownDone";

    private CountDownAsyncTask mCountDownAsyncTask;
    private TextView mCountDownNumber;
    private int mCountDown = 0xff;

    // lint is failing to see that setOnSystemUiVisibilityChangeListener is wrapped in
    // if (Build.VERSION.SDK_INT >= 11).
    @TargetApi(11)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Window window = getWindow();
        window.setBackgroundDrawable(null);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_count_down);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int scale;
        if (displayMetrics.heightPixels > displayMetrics.widthPixels) {
            scale = displayMetrics.heightPixels;
        } else {
            scale = displayMetrics.widthPixels;
        }
        mCountDownNumber = (TextView) findViewById(R.id.countDownNumber);
        mCountDownNumber.setTextSize(((float) scale) / 5);

        mCountDownAsyncTask = new CountDownAsyncTask();

        if (savedInstanceState != null && savedInstanceState.getBoolean(KEY_COUNT_DOWN_DONE, false)) {
            done();
        } else {
            mCountDownAsyncTask.execute(
                    Integer.parseInt(prefs.getString(getString(R.string.pref_countdown_seconds), "5")));
        }

        RelativeLayout frameRoot = (RelativeLayout) findViewById(R.id.frameRoot);
        frameRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                cancel();
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= 16) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= 14) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        if (Build.VERSION.SDK_INT >= 11) {
            frameRoot.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    /* If the nav bar comes back while the countdown is active,
                       that means the user clicked on the screen. Showing the
                       test dialog also triggers this, so filter on countdown */
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0 && mCountDown > 0) {
                        cancel();
                    }
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_COUNT_DOWN_DONE, mCountDown == 0);
        if (mCountDown > 0) {
            // cancel the countdown, it'll get restarted when the Activity comes back
            mCountDownAsyncTask.cancel(true);
        }
    }

    private void cancel() {
        mCountDownAsyncTask.cancel(true);
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void done() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private class CountDownAsyncTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            mCountDown = values[0];
            if (values[0] > 0) {
                mCountDownNumber.setText(String.valueOf(values[0]));
            } else {
                done();
            }
        }

        @Override
        protected Void doInBackground(Integer... count) {
            try {
                int countdown = count[0];
                while (countdown >= 0) {
                    publishProgress(countdown);
                    countdown--;
                    Thread.sleep(1000);
                    if (isCancelled()) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                // Silent block
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
