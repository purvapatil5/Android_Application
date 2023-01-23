package com.example.lifestyle_management;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;


import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LandingPage extends AppCompatActivity {
    CardView date_card, break_card, routine_card;
    Button log_out;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        date_card =  findViewById(R.id.date_card);
        break_card = findViewById(R.id.breaks_card);
        log_out = findViewById(R.id.logOut);

        date_card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               openCurrentDatePage();

            }
        });
        break_card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
              openBreaksPage();

            }
        });
        log_out.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sp = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("logged", true); // set it to true when the user is logged out
                editor.putString("Email" , null);
                editor.apply();
                startActivity(new Intent(LandingPage.this , MainActivity.class));
            }
        });
       this.permissionLauncher();
    }
    public void openCurrentDatePage(){
        Intent intent = new Intent(this,CurrentDatePage.class);
        startActivity(intent);
    }
    public void openBreaksPage(){
        Intent intent = new Intent(this,BreaksPage.class);
        startActivity(intent);
    }
    
   @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
        if (sharedPreferences.getBoolean("logged", true)) {
            finish();
        }
        else
        {
            moveTaskToBack(true);
        }

    }
    private void permissionLauncher(){
        System.out.println("landingPage + Inside launcher");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel("notify_001");
            // notificationChannel.
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notify_001", "channel name", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notification for LifeStyleMangement");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

    }

}
