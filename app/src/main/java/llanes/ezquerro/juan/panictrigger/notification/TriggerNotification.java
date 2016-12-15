package llanes.ezquerro.juan.panictrigger.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import llanes.ezquerro.juan.panictrigger.R;

public class TriggerNotification {
    private static final int TRIGGER_NOTIFICATION_ID = 0x0caca0;

    private Context mContext;
    private NotificationManager mNotificationManager;

    public TriggerNotification(Context context) {
        mContext = context;
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void show(boolean isTest) {
        String title;
        String content;

        if (isTest) {
            title = mContext.getString(R.string.runned_test_notification_title);
            content = mContext.getString(R.string.runned_test_notification_content);
        } else {
            title = mContext.getString(R.string.runned_notification_title);
            content = mContext.getString(R.string.runned_notification_content);
        }

        Notification panic = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .build();

        mNotificationManager.notify(TRIGGER_NOTIFICATION_ID, panic);
    }
}
