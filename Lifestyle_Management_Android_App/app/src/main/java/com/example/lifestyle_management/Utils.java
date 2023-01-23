package com.example.lifestyle_management;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utils {
    private Context _context;
    private static String DATA_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static char TIME_FORMATTER = 'T';
    String timeToNotify,timeIn12HrFormat;

    public Utils(Context context){
        this._context = context;
    }

    public void manageAlarms(String curUserEmail){
        System.out.println("UTILS In Untils Manage Alarms");
        DatabaseHelper databasehelper = new DatabaseHelper(this._context);
        Cursor cursor = databasehelper.getBreaksInformation();
        while(cursor.moveToNext()){
            String break_title = cursor.getString(1);
            String break_date = cursor.getString(2);
            String break_time = cursor.getString(3);
            int break_requestCode = cursor.getInt(4);
            int isAlertOn = cursor.getInt(5);
            String userEmail = cursor.getString(6);

            System.out.println("UTILS " + userEmail + " , " + curUserEmail);

            if((isAlertOn == 1)){
                if(!userEmail.equals(curUserEmail)){
                    AlarmManager am = (AlarmManager)this._context.getSystemService(Context.ALARM_SERVICE);                   //assigning alarm manager object to set alarm
                    Intent intent = new Intent(this._context, AlarmBroadcast.class);
                    intent.putExtra("event", break_title);                                                       //sending data to alarm class to create channel and notification
                    intent.putExtra("date", break_date);
                    intent.putExtra("time", break_time);
                    intent.putExtra("requestCode", break_requestCode);
                    System.out.println("UTILS User email is different");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this._context, break_requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
                    am.cancel(pendingIntent);
                    pendingIntent.cancel();
                }else{
                    String dateAndTime = getFormattedDateAndTime(break_date,break_time);
                    DateFormat formatter = new SimpleDateFormat(DATA_FORMAT);
                    Date curDate = new Date();
                    System.out.println("UTILS dateAndTime"+formatter.format(curDate));
                    if(formatter.format(curDate).compareTo(dateAndTime) > 0){ // d1.compareTo(d2) > 0
                        continue;
                    }
                    setAlarm(break_title,break_date,break_time,break_requestCode,true);

                }
            }

        }
    }
    public void setAlarm(String break_title, String break_date, String break_time, int break_requestCode,boolean isEditFlow){
        AlarmManager am = (AlarmManager)this._context.getSystemService(Context.ALARM_SERVICE);                   //assigning alarm manager object to set alarm
        Intent intent = new Intent(this._context, AlarmBroadcast.class);

        intent.putExtra("event", break_title);                                                      //sending data to alarm class to create channel and notification
        intent.putExtra("date", break_date);
        intent.putExtra("time", break_time);
        if(break_requestCode == -1){
            break_requestCode = generateUUID();
        }
        intent.putExtra("requestCode", break_requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this._context, break_requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
        DateFormat formatter = new SimpleDateFormat(DATA_FORMAT);
        String dateAndTime = getFormattedDateAndTime(break_date,break_time);
        Date date1 = null;
        try {
            date1 = formatter.parse(dateAndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("PRINT_DATE UTILS "+ date1.getTime());
        am.setExact(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);

        // are the below three lines of code necessary after setting an alarm
//                       Intent intentBack = new Intent(getContext(), BreaksPage.class); //this intent will be called once the setting alarm is complete
//                        Toast.makeText(getContext(), "Alarm", Toast.LENGTH_SHORT).show();
//                        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

    }

    public String getFormattedDateAndTime(String break_date, String break_time) {
        System.out.println("In UTILS getFormattedDateAndTime");
        get12HrFormattedTime(break_time);
        String input = break_date + " " +timeIn12HrFormat;
        System.out.println("UTILS Formatted 12 hr date and time "+ input);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("yyyy-M-d'T'H:m:ss");
        Date date = null;
        String output = null;
        try{
            //Converting the input String to Date
            date= df.parse(input);
            //Changing the format of date and storing it in String
            output = outputformat.format(date);
            //Displaying the date
            System.out.println(" PRINT_DATE UTILS TIME "+ output);
        }catch(ParseException pe){
            pe.printStackTrace();
        }
//        dateAndTime = output;
        return output;
    }

    public void get12HrFormattedTime(String time){
        String[] arr1 = time.trim().split(":");
        String[] arr2 = arr1[1].trim().split(" ");
        timeIn12HrFormat = arr1[0] + ":" + arr2[0] + ":00 " + arr2[1];
    }
    private int generateUUID(){
        UUID idOne = UUID.randomUUID();
        String str=""+idOne;
        int uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        return Integer.parseInt(str);
    }
}
