package com.guna.todayschedule;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by POUCOTM on 2016-01-25.
 */
public class TodaySchedule extends AppWidgetProvider {

    static final String[] projection = new String[] {
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // today
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = startTime.getTime();
        String today = new SimpleDateFormat("yyyyMMdd").format(date);
        try {
            startTime.setTime(sdf.parse(today));
            endTime.setTime(sdf.parse(today));
            endTime.add(Calendar.DATE, 1);
        } catch (ParseException e) { e.printStackTrace(); }

        // for including recurring events
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://com.android.calendar/instances/when/" + startTime.getTimeInMillis() + "/" + endTime.getTimeInMillis()),
                projection, null, null, CalendarContract.Instances.DTSTART + " ASC");

        StringBuffer sb = new StringBuffer();
        SimpleDateFormat tsdf = new SimpleDateFormat("HH:mm");
        if (cursor.moveToFirst()) {
            do {
                Date td = new Date(cursor.getLong(3));
                sb.append(tsdf.format(td).toString() + "  " + cursor.getString(1) + "\n");
            } while (cursor.moveToNext());
        }

        for(int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews remote = new RemoteViews(context.getPackageName(), R.layout.todayschedule_widget);
            remote.setTextViewText(R.id.tsche, sb.toString());
            appWidgetManager.updateAppWidget(appWidgetIds[i], remote);
        }
    }
}
