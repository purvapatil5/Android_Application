package com.example.lifestyle_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccount extends AppCompatActivity {
    TextView textview;
    EditText name,email,password,confirm_password;
    Button submit;
    private DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_acc_activity);

        textview=findViewById(R.id.create_account);
        name=findViewById(R.id.full_name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confirm_password=findViewById(R.id.confirm_password);

        submit=findViewById(R.id.create_submit_button);
        myDB = new DatabaseHelper(this);
        insertUser();

        }

    private void insertUser(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkData = checkDataEntered();
                if(checkData==true) {
                    boolean var = myDB.registerUser(name.getText().toString(), email.getText().toString(), password.getText().toString());
                    if (var) {
                        Toast.makeText(CreateAccount.this, "User Registered Successfully !!", Toast.LENGTH_SHORT).show();
                        boolean doesTableExist = myDB.doesTableExist("BREAKS_TABLE");
                        if(!doesTableExist) myDB.createBreaksTable();
                        myDB.addBreak("Work Break", "2022-12-31","2:30 pm", 121,1, email.getText().toString());
                        myDB.addBreak("Gym Break", "2022-12-29","6:30 am", 162,0,email.getText().toString());
                        myDB.addBreak("Water Break", "2022-12-30","11:30 am", 741,1,email.getText().toString());
                        startActivity(new Intent(CreateAccount.this , MainActivity.class));
                    } else
                        Toast.makeText(CreateAccount.this, "User Already Exists !!", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
    boolean isMatchPassword(EditText password,EditText confirm_password){
        CharSequence str1=password.getText().toString();
        CharSequence str2=confirm_password.getText().toString();
        if(TextUtils.equals(str1,str2))
            return true;
        else
            return false;
    }

    boolean checkDataEntered() {

        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern lowercase = Pattern.compile("[a-z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern spc_charac = Pattern.compile("(?=.*[@#$%^&+=])");

        if (isEmpty(name)) {
            name.requestFocus();
            name.setError("Enter your full name");
            return false;
        }

        if (isEmpty(email)) {
            email.requestFocus();
            email.setError("Enter your email!");
            return false;
        }

        if (isEmail(email) == false) {
            email.requestFocus();
            email.setError("Enter valid email!");
            return false;
        }

        if (isEmpty(password)) {
            password.requestFocus();
            email.setError("Enter the password");
            return false;
        }

        if (isEmpty(confirm_password)) {
            confirm_password.requestFocus();
            email.setError("Confirm the password");
            return false;
        }

        // if lowercase character is not present
        if (!lowercase.matcher(password.getText().toString()).find()) {
            password.requestFocus();
            password.setError("Atleast 1 lowecase alphabet is required");
            return false;
        }
        // if uppercase character is not present
        if (!uppercase.matcher(password.getText().toString()).find()) {
            password.requestFocus();
            password.setError("Atleast 1 uppercase alphabet is required");
            return false;
        }
        // if digit is not present
        if (!digit.matcher(password.getText().toString()).find()) {
            password.requestFocus();
            password.setError("Atleast 1 digit is required");
            return false;
        }
        if (!spc_charac.matcher(password.getText().toString()).find()) {
            password.requestFocus();
            password.setError("Atleast 1 special character is required");
            return false;
        }
        // if password length is less than 8
        if (password.length() < 8) {
            password.requestFocus();
            password.setError("Atleast 8 characters");
            return false;
        }
        if(isMatchPassword(password,confirm_password)==false){
            confirm_password.requestFocus();
            confirm_password.setError("Password does not match");
            return false;
        }

        return true;
    }




}
