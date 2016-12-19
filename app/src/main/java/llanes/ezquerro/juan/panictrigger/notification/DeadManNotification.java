package llanes.ezquerro.juan.panictrigger.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import llanes.ezquerro.juan.panictrigger.R;
import llanes.ezquerro.juan.panictrigger.activities.PanicActivity;
import llanes.ezquerro.juan.panictrigger.constants.PanicTriggerConstants;

public class DeadManNotification {
    private static final int DEAD_NOTIFICATION_ID = 0xdead;
    private static boolean IS_VISIBLE = false;

    private Context mContext;
    private NotificationManager mNotificationManager;

    public DeadManNotification(Context context) {
        mContext = context;
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Notification dead() {
        Intent trigger = new Intent(mContext, PanicActivity.class);
        trigger.putExtra(PanicTriggerConstants.CANCEL_DEAD_MAN, true);

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
                .setContentTitle(mContext.getString(R.string.dead_man_notification))
                .setContentText(mContext.getString(R.string.dead_man_notification_content))
                .build();
    }

    public void display(Boolean visible) {
        IS_VISIBLE = visible;

        if (IS_VISIBLE) {
            mNotificationManager.notify(DEAD_NOTIFICATION_ID, dead());
        } else {
            mNotificationManager.cancel(DEAD_NOTIFICATION_ID);
        }
    }
}
