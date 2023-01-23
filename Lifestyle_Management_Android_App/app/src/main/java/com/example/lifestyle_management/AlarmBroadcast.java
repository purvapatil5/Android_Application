package com.example.lifestyle_management;


import static android.content.Context.MODE_PRIVATE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AlarmBroadcast extends BroadcastReceiver {

    String text;
    String date;
    String time;
    String parsedTime;
    int requestCode;
    String email;
    DatabaseHelper databasehelper;
    ArrayList<Breaks_Storage_Model> Breaks_Storage_ModelArrayList;
    PendingIntent pendingIntent;
    private static char TIME_FORMATTER = 'T';
    private static String DATA_FORMAT = "yyyy-M-d'T'HH:mm:ss";
    String currentDate = new SimpleDateFormat("yyyy-M-d", Locale.getDefault()).format(new Date());

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);                   //assigning alarm manager object to set alarm
        databasehelper = new DatabaseHelper(context);

        SharedPreferences sp = context.getSharedPreferences("login", MODE_PRIVATE);
        boolean hasLoggedIn = sp.getBoolean("logged", true);
        if (!hasLoggedIn) {
            email = sp.getString("Email", "null");
        }
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Cursor cursor = databasehelper.getALlBreaksDataIfAlertIsOn();
            Breaks_Storage_ModelArrayList = new ArrayList<Breaks_Storage_Model>();
            if (cursor.getCount() == 0) {
                Breaks_Storage_ModelArrayList.add(null);
            } else {
                while (cursor.moveToNext()) {
                    Breaks_Storage_ModelArrayList.add(new Breaks_Storage_Model(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5)));
                    int size = Breaks_Storage_ModelArrayList.size();

                    for (int i = 0; i < size; i++) {
                        if (Breaks_Storage_ModelArrayList.get(i).getBreak_date().equals(currentDate))
                        {
                            text = Breaks_Storage_ModelArrayList.get(i).getBreak_name();
                            date = Breaks_Storage_ModelArrayList.get(i).getBreak_date();
                            time = Breaks_Storage_ModelArrayList.get(i).getBreak_time();
                            requestCode = Breaks_Storage_ModelArrayList.get(i).getBreak_requestCode();
                            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
                            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");

                            Date userTime = null;
                            try {
                                userTime = parseFormat.parse(time);
                                parsedTime = displayFormat.format(userTime);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Intent intentPending = new Intent(context, AlarmBroadcast.class);
                            Bundle b = new Bundle();
                            b.putString("event", text);
                            b.putString("date", date);
                            b.putString("time", parsedTime);
                            b.putInt("requestCode", requestCode);
                            intentPending.putExtras(b);
                            pendingIntent = PendingIntent.getBroadcast(context, requestCode, intentPending, PendingIntent.FLAG_IMMUTABLE);

                            String dateandtime = getDateInFormat(date, parsedTime);
                            DateFormat formatter = new SimpleDateFormat(DATA_FORMAT);
                            try {
                                Date date1 = formatter.parse(dateandtime);
                                System.out.println(date1.getTime());
                                am.setExact(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        }
        else
        {
            setAlarm(context, intent);
        }
    }

    public void setAlarm(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
            text = bundle.getString("event");
            date = bundle.getString("date") + " " + bundle.getString("time");
            requestCode = bundle.getInt("requestCode");

        //Click on Notification
        Intent intent1 = new Intent(context, GenerateNotification.class);

        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("message", text);
        System.out.println("Put text :" + text);
        //Notification Builder
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent1, PendingIntent.FLAG_IMMUTABLE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "notify_001");
        //here we set all the properties for the notification
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.activity_generate_notification);
        contentView.setImageViewResource(R.id.icon, R.mipmap.ic_launcher);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
        contentView.setOnClickPendingIntent(R.id.flashButton, pendingSwitchIntent);
        contentView.setTextViewText(R.id.message, text);
        contentView.setTextViewText(R.id.date, date);
        mBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        mBuilder.setAutoCancel(true);
        mBuilder.setOngoing(false);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
        mBuilder.setContent(contentView);
        mBuilder.setContentIntent(pendingIntent);
        //we have to create notification channel after api level 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "notify_001";
            NotificationChannel channel = new NotificationChannel("notify_001", "channel name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }
        Notification notification = mBuilder.build();
        notificationManager.notify(requestCode, notification);
    }

    String getDateInFormat(String date, String time) {
        return date + TIME_FORMATTER + time +":00";
    }

}
