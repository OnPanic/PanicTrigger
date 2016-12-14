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
    public static final int PANIC_NOTIFICATION_ID = 0xbadc0d3;

    private Context mContext;
    private NotificationManager mNotificationManager;

    public PanicNotification(Context context) {
        mContext = context;
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void show() {
        Intent trigger = new Intent(mContext, PanicActivity.class);

        PendingIntent click =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        trigger,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Notification panic = new NotificationCompat.Builder(mContext)
                .setContentIntent(click)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setContentTitle(mContext.getString(R.string.panic_notification))
                .setContentText(mContext.getString(R.string.panic_notification_content))
                .build();

        mNotificationManager.notify(PANIC_NOTIFICATION_ID, panic);
    }

    public void hide() {
        mNotificationManager.cancel(PANIC_NOTIFICATION_ID);
    }
}
