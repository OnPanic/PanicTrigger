package llanes.ezquerro.juan.panictrigger.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import llanes.ezquerro.juan.panictrigger.R;
import llanes.ezquerro.juan.panictrigger.activities.PanicActivity;

public class PanicWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(context, PanicActivity.class);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.panic_widget);
        views.setOnClickPendingIntent(R.id.panic_widget_btn, pending);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

