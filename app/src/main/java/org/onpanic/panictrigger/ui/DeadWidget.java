package org.onpanic.panictrigger.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.onpanic.panictrigger.R;
import org.onpanic.panictrigger.activities.DeadWidgetConfigureActivity;
import org.onpanic.panictrigger.activities.PanicActivity;
import org.onpanic.panictrigger.constants.PanicTriggerConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DeadWidget extends AppWidgetProvider {
    private static PendingIntent panic;
    private static int id;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        id = appWidgetId;

        Long time = (DeadWidgetConfigureActivity.loadDeadTimePref(context, appWidgetId) * 1000L)
                + System.currentTimeMillis();

        Date date = new Date(time);
        SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat day = new SimpleDateFormat("EEE", Locale.getDefault());

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.dead_widget);
        views.setTextViewText(R.id.hour, hour.format(date));
        views.setTextViewText(R.id.day, day.format(date));
        Intent intent = new Intent(context, DeadWidget.class);
        intent.setAction(PanicTriggerConstants.CANCEL_DEAD_MAN);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.alarmWidget, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);

        Intent trigger = new Intent(context, DeadWidget.class);
        trigger.setAction(PanicTriggerConstants.RUN_DEAD_MAN);

        panic = PendingIntent.getBroadcast(
                context,
                0,
                trigger,
                0
        );

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= 19) {
            manager.setExact(AlarmManager.RTC_WAKEUP, time, panic);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, time, panic);
        }

        Toast.makeText(context, R.string.click_widget_for_discard, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            DeadWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // VOid
    }

    @Override
    public void onDisabled(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (panic != null) manager.cancel(panic);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        RemoteViews views = new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.dead_widget);
        views.setImageViewResource(R.id.alarm_image, R.drawable.no_alarm);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(id, views);

        if (action.equals(PanicTriggerConstants.CANCEL_DEAD_MAN)) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (panic != null) manager.cancel(panic);
            Toast.makeText(context, R.string.dead_man_canceled, Toast.LENGTH_LONG).show();
        } else if (action.equals(PanicTriggerConstants.RUN_DEAD_MAN)) {
            Intent panic = new Intent(context, PanicActivity.class);
            panic.putExtra(PanicTriggerConstants.RUN_DEAD_MAN, true);
            context.startActivity(panic);
        }
    }
}

