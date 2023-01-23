package com.example.lifestyle_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class CurrentDatePage extends AppCompatActivity {
    TodayTaskAdapter taskAdapter;
    ArrayList<Breaks_Storage_Model> Breaks_Storage_ModelArrayList;
    ArrayList<Task_Storage_Model> Task_Storage_ModelArrayList;
    RecyclerView taskRV;
    DatabaseHelper databasehelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_date_page);

        taskRV = findViewById(R.id.idTasks);
        Breaks_Storage_ModelArrayList = new ArrayList<Breaks_Storage_Model>();
        databasehelper = new DatabaseHelper(this);
        String currentDate = new SimpleDateFormat("yyyy-M-d", Locale.getDefault()).format(new Date());
        Task_Storage_ModelArrayList = new ArrayList<Task_Storage_Model>();

        Cursor cursor = databasehelper.getALlBreaksData();
        if(cursor.getCount() == 0){
            Toast.makeText(this.getApplicationContext(),"No tasks for today", Toast.LENGTH_SHORT).show();
        }else {
            while(cursor.moveToNext()){
                Breaks_Storage_ModelArrayList.add(new Breaks_Storage_Model(cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getInt(4), cursor.getInt(5)));
            }

            int size = Breaks_Storage_ModelArrayList.size();

            for(int i=0; i<size;i++)
            {
                System.out.println(Breaks_Storage_ModelArrayList.get(i).getBreak_date());
                if(Breaks_Storage_ModelArrayList.get(i).getBreak_date().equals(currentDate))
                {
                    String task_name = Breaks_Storage_ModelArrayList.get(i).getBreak_name();
                    String task_time = Breaks_Storage_ModelArrayList.get(i).getBreak_time();
                    String task_date = Breaks_Storage_ModelArrayList.get(i).getBreak_date();
                    Task_Storage_ModelArrayList.add(new Task_Storage_Model(task_name, task_time,task_date));
                }
            }

        }

        // we are initializing our adapter class and passing our arraylist to it.
        if(Task_Storage_ModelArrayList.size() != 0) {
            taskAdapter = new TodayTaskAdapter(this, Task_Storage_ModelArrayList);
        }
        else
        {
            Task_Storage_ModelArrayList.add(new Task_Storage_Model("No Tasks For Today", null ,null));
            taskAdapter = new TodayTaskAdapter(this, Task_Storage_ModelArrayList);
            Toast.makeText(this.getApplicationContext(),"No tasks for today", Toast.LENGTH_SHORT).show();
        }
        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        taskRV.setLayoutManager(linearLayoutManager);
        taskRV.setAdapter(taskAdapter);

    }



}
