package llanes.ezquerro.juan.panictrigger.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import llanes.ezquerro.juan.panictrigger.R;
import llanes.ezquerro.juan.panictrigger.activities.PanicActivity;

public class PanicNotification {
    private static final int PANIC_NOTIFICATION_ID = 0xbadc0d3;
    private static boolean IS_VISIBLE = false;

    private Context mContext;
    private NotificationManager mNotificationManager;

    public PanicNotification(Context context) {
        mContext = context;
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Notification panic() {
        Intent trigger = new Intent(mContext, PanicActivity.class);

        PendingIntent click =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        trigger,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        return new NotificationCompat.Builder(mContext)
                .setContentIntent(click)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setContentTitle(mContext.getString(R.string.panic_notification))
                .setContentText(mContext.getString(R.string.panic_notification_content))
                .build();
    }

    public void display(Boolean visible) {
        if (visible) {
            mNotificationManager.notify(PANIC_NOTIFICATION_ID, panic());
            IS_VISIBLE = visible;
        } else {
            mNotificationManager.cancel(PANIC_NOTIFICATION_ID);
            IS_VISIBLE = visible;
        }
    }

    public boolean isVisible() {
        return IS_VISIBLE;
    }
}
