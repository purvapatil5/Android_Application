package com.example.lifestyle_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class BreaksPage extends AppCompatActivity implements AddBreaksPage.AddBreaksPageListener, EditBreaksPage.EditBreaksPageListener {
    private FloatingActionButton addBreakBtn;
    BreakAdapter breakAdapter;
    ArrayList<Breaks_Storage_Model> Breaks_Storage_ModelArrayList;
    RecyclerView breakRV;
    DatabaseHelper databasehelper;
    private static char TIME_FORMATTER = 'T';
    private static String DATA_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breaks_page);

        breakRV = findViewById(R.id.idRVBreaks);
        breakRV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editDialog();
            }
        });

        databasehelper = new DatabaseHelper(BreaksPage.this);
        Cursor cursor = databasehelper.getALlBreaksData();
        Breaks_Storage_ModelArrayList = new ArrayList<Breaks_Storage_Model>();
        if(cursor.getCount() == 0){
            Toast.makeText(this.getApplicationContext(),"No Breaks data to display", Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()){
                Breaks_Storage_ModelArrayList.add(new Breaks_Storage_Model(cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3), cursor.getInt(4), cursor.getInt(5)));
            }
        }

        addBreakBtn = (FloatingActionButton) findViewById(R.id.fab);
        addBreakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDialog();
            }
        });

        // we are initializing our adapter class and passing our arraylist to it.
        breakAdapter = new BreakAdapter(this, Breaks_Storage_ModelArrayList);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        breakRV.setLayoutManager(linearLayoutManager);
        breakRV.setAdapter(breakAdapter);
    }


    private void openAddDialog() {
      AddBreaksPage addBreaksPage = new AddBreaksPage();
//      Bundle b = new Bundle();
//      b.putParcelableArrayList("breaks_data", (ArrayList<? extends Parcelable>) Breaks_Storage_ModelArrayList);
//      addBreaksPage.setArguments(b);
      addBreaksPage.show(getSupportFragmentManager(), "Add breaks");
    }

    private void editDialog(){
        EditBreaksPage editBreaksPage = new EditBreaksPage();
        editBreaksPage.show(getSupportFragmentManager(),"Edit breaks");

    }

    @Override
    public void saveBreaksData(String break_title,String date, String time,int requestCode) {
        // This data will be received from add breaks dialog
        if(break_title.isEmpty() || date.isEmpty() || time.isEmpty() ){
            return;
        }

        databasehelper = new DatabaseHelper(BreaksPage.this);
        databasehelper.addBreak(break_title,date,time,requestCode,1,null);

        Cursor cursor = databasehelper.getALlBreaksData();
        Breaks_Storage_ModelArrayList = new ArrayList<Breaks_Storage_Model>();
        if(cursor.getCount() == 0){
            Toast.makeText(this.getApplicationContext(),"No Breaks data to display", Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()){
                Log.println(Log.INFO,"DB_DATA",cursor.getString(1));
                Breaks_Storage_ModelArrayList.add(new Breaks_Storage_Model(cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5)));
            }
        }
        breakAdapter = new BreakAdapter(this, Breaks_Storage_ModelArrayList);
        breakRV.setAdapter(breakAdapter);

        int isAlert=1;
        setAlarm(this,break_title,date,time,requestCode,isAlert);

    }

    @Override
    public void updateBreaksData(String break_title,String date,String time, int position,int requestCode, String breakID,int isAlertOn){

//        String date = year + "-" + month + "-" +day;
//        String time = hour + ":" + min + " " +am_pm;

        Breaks_Storage_ModelArrayList.set(position,new Breaks_Storage_Model(breakID,break_title, time, date, requestCode,isAlertOn));
        databasehelper = new DatabaseHelper(BreaksPage.this);
        databasehelper.updateBreak(breakID,break_title,date,time,requestCode,isAlertOn);

        Cursor cursor = databasehelper.getALlBreaksData();
        Breaks_Storage_ModelArrayList = new ArrayList<Breaks_Storage_Model>();
        if(cursor.getCount() == 0){
            Toast.makeText(this.getApplicationContext(),"No Breaks data to display", Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()){
                Log.println(Log.INFO,"DB_DATA",cursor.getString(1));
                Breaks_Storage_ModelArrayList.add(new Breaks_Storage_Model(cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5)));
            }
        }

        breakRV.getAdapter().notifyItemChanged(position);

        breakAdapter = new BreakAdapter(this, Breaks_Storage_ModelArrayList);
        breakRV.setAdapter(breakAdapter);

        if(isAlertOn == 1){

            setAlarm(this,break_title,date,time,requestCode,isAlertOn);

        }
        if (isAlertOn == 0){

            AlarmManager am = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);                   //assigning alarm manager object to set alarm
            Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
            am.cancel(pendingIntent);
            pendingIntent.cancel();
        }

    }

    private void setAlarm(Context context, String break_title, String date, String time, int requestCode, int isBreakAlertOn) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);                   //assigning alarm manager object to set alarm
        Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
        Bundle b= new Bundle();
        b.putString("event", break_title);
        b.putString("date", date);
        b.putString("time", time);
        b.putInt("requestCode",requestCode);
//        b.putString("channelId",channelId);
        b.putInt("isBreakAlertOn",isBreakAlertOn);
        intent.putExtras(b);
        System.out.println("Set Alarm isAlertOn:"+isBreakAlertOn);
        System.out.println("Bundle alert value:"+b.getInt("isBreakAlertOn"));

        Bundle bundle = intent.getExtras();
        int isAlert = bundle.getInt("isBreakAlertOn");
        System.out.println("Is ALert: "+isAlert);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
        String dateandtime = getDateInFormat(date,time);
        //String testDate = "2022-10-29"+'T'+timeToNotify+":00";
        System.out.println("Break page event : "+break_title);
        System.out.println("Break page datetime : "+dateandtime);
        DateFormat formatter = new SimpleDateFormat(DATA_FORMAT);

        try {
            Date date1 = formatter.parse(dateandtime);
            System.out.println(date1.getTime());
            am.setExact(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intentBack = new Intent(getApplicationContext(), BreaksPage.class); //this intent will be called once the setting alarm is complete
        Toast.makeText(getApplicationContext(), "Alert updated for  "+break_title, Toast.LENGTH_SHORT).show();
        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentBack);                                                                  //navigates from adding reminder activity to mainactivity
    }
    String getDateInFormat(String date, String time) {
        return date + TIME_FORMATTER + time +":00";
    }


    @Override
    public  void deleteBreaksData(){
        databasehelper = new DatabaseHelper(BreaksPage.this);

        Cursor cursor = databasehelper.getALlBreaksData();
        Breaks_Storage_ModelArrayList = new ArrayList<Breaks_Storage_Model>();
        if(cursor.getCount() == 0){
            Toast.makeText(this.getApplicationContext(),"No Breaks data to display", Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()){
                Log.println(Log.INFO,"DB_DATA",cursor.getString(1));
                Breaks_Storage_ModelArrayList.add(new Breaks_Storage_Model(cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5)));
            }
        }
        breakAdapter = new BreakAdapter(this, Breaks_Storage_ModelArrayList);
        breakRV.setAdapter(breakAdapter);

    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(BreaksPage.this, LandingPage.class));
    }

}