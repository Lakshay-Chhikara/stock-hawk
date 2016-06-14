package com.sam_chordas.android.stockhawk;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.ui.StockChangeActivity;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (StockTaskService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(context);
            mAppWidgetManager.notifyAppWidgetViewDataChanged(
                    mAppWidgetManager.getAppWidgetIds(new ComponentName(context,
                            NewAppWidget.class)),
                    R.id.recycler_view);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent myStocksWidgetServiceIntent = new Intent(context, MyStocksWidgetService.class);
            myStocksWidgetServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            myStocksWidgetServiceIntent.setData(
                    Uri.parse(myStocksWidgetServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.new_app_widget);
            remoteViews.setRemoteAdapter(R.id.recycler_view, myStocksWidgetServiceIntent);
            // TODO : Create and empty view and set it here
            //remoteViews.setEmptyView();

            Intent stockChangeActivityIntent = new Intent(context, StockChangeActivity.class);
            stockChangeActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            stockChangeActivityIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            stockChangeActivityIntent.setData(Uri.parse(
                    stockChangeActivityIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent stockChangeActivityPendingIntent = PendingIntent.getActivity(
                    context, 0, stockChangeActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            /*final PendingIntent stockChangeActivityPendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(stockChangeActivityIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);*/
            remoteViews.setPendingIntentTemplate(R.id.recycler_view,
                    stockChangeActivityPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}

