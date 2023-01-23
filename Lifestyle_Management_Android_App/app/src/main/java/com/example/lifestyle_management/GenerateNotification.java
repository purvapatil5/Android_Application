package com.example.lifestyle_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GenerateNotification extends AppCompatActivity {
    TextView textView;
    Button dismiss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_notification);
        textView=findViewById(R.id.message);
        Bundle bundle=getIntent().getExtras();
        textView.setText(bundle.getString("message"));
        System.out.println("GET EXTRA "+bundle.getString("message"));
        dismiss= (Button)findViewById(R.id.flashButton);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dismissScreen = new Intent(GenerateNotification.this, LandingPage.class);
                startActivity(dismissScreen);
            }
        });
    }

}