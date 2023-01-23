package com.example.lifestyle_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditBreaksPage extends AppCompatDialogFragment {

    private static String DATA_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static char TIME_FORMATTER = 'T';
    private EditBreaksPage.EditBreaksPageListener listener;
    private DatePickerDialog datePickerDialog;
    private String picked_am_pm,title,date,time;
    private AlertDialog.Builder builder;
    private  AlertDialog dialog;


    EditText editTitle;
    Button dateButton, timeButton;
    ImageView deleteButton;
    //String timeToNotify;
   // private int picked_day, picked_month, picked_year,picked_hour,picked_minute;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_break_dialog,null);
        builder.setView(dialogView)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            title = editTitle.getText().toString().trim();
                            date = dateButton.getText().toString().trim();
                            String out_time = timeButton.getText().toString().trim();
                            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
                            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
                        try {
                            Date userTime = parseFormat.parse(out_time);
                            time = displayFormat.format(userTime);
                            System.out.println("Input time is :"+time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        int position=getArguments().getInt("position");
                        int requestCode = getArguments().getInt("Break_requestCode");
                        int isBreakAlertOn = getArguments().getInt("Break_Alarm_On");
                        String break_ID = getArguments().getString("Break_ID");
                        System.out.println("Edit submit on click:"+time);


//                        String title = editTitle.getText().toString().trim();                               //access the data from the input field
//                        String date = dateButton.getText().toString().trim();                                 //access the date from the choose date button
//                        String time = timeButton.getText().toString().trim();
//                        String breakTitle = break_title.getText().toString();
//                        listener.saveBreaksData(breakTitle, picked_year, picked_month, picked_day, picked_hour,
//                                picked_minute, picked_am_pm);


                        if (title.isEmpty()) {
                            Toast.makeText(getContext(), "Please Enter text", Toast.LENGTH_SHORT).show();   //shows the toast if input field is empty
                        }
                        else if(date.equals("date") && time.equals("time")){
                            //shows toast if date and time are not selected
                            Toast.makeText(getContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                        }
                        else if(time.equals("time") && !date.equals("date")){
                            Toast.makeText(getContext(), "Please select time", Toast.LENGTH_SHORT).show();

                        }
                        else if(!time.equals("time") && date.equals("date")){
                            Toast.makeText(getContext(), "Please select date", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            setAlarm(getContext(),title, date, time, requestCode);
                            Toast.makeText(getContext(), "Alarm set for selected date and time", Toast.LENGTH_SHORT).show();

                        }
                        listener.updateBreaksData(title,date,time,position,requestCode,break_ID,isBreakAlertOn);
                    }

                });

        initDatePicker(dialogView);
        String Break_Name= getArguments().getString("Break_Name");
        String Break_date= getArguments().getString("Break_date");
        String Break_time= getArguments().getString("Break_time");


        editTitle=(EditText)dialogView.findViewById(R.id.editTitle) ;
        dateButton=(Button)dialogView.findViewById(R.id.btnDate);
        timeButton=(Button)dialogView.findViewById(R.id.btnTime);
        deleteButton= (ImageView) dialogView.findViewById(R.id.delete_btn);
        editTitle.setText(Break_Name);
        dateButton.setText(Break_date);
        timeButton.setText(Break_time);

        //selectDate(dialogView);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
        //time_picker_btn = (Button) dialogView.findViewById(R.id.time_picker);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBreak();
            }
        });
        if(savedInstanceState != null){
            editTitle.setText(savedInstanceState.getString("break_title", Break_Name));
            dateButton.setText(savedInstanceState.getString("break_date", Break_date));
            timeButton.setText(savedInstanceState.getString("break_time", Break_time));
        }
        dialog = builder.create();
        return dialog;
    }

    private void deleteBreak() {
        String break_ID = getArguments().getString("Break_ID");
        DatabaseHelper db = new DatabaseHelper(this.getContext());
        db.deleteBreak(break_ID);
        dialog.cancel();
        listener.deleteBreaksData();
    }


    private void selectTime() {                                                                     //this method performs the time picker task
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                //timeToNotify = i + ":" + i1 + ":00";                                                        //temp variable to store the time to set alarm
                timeButton.setText(FormatTime(i, i1));                                               //sets the button text as selected time
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }
    private void openDatePicker() {
        datePickerDialog.show();
    }

    private void initDatePicker(View dialogView) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                picked_day = day;
//                picked_month = month+1;
//                picked_year = year;
                dateButton.setText(year + "-" + (month+1) + "-"+ day);

            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(dialogView.getContext(),style,dateSetListener,year,month,day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
    }

//    //String getDateInFormat(String date) {
//        return date + TIME_FORMATTER + timeToNotify;
//    }
 //   private void selectDate() {                                                                     //this method performs the date picker task
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                dateButton.setText(year + "-" + (month + 1) + "-" + day);//sets the selected date as test for button
//                picked_year=year;
//                picked_month=(month+1);
//                picked_day=day;
//            }
//
//        }, year, month, day);
//        datePickerDialog.show();
//    }
    public String FormatTime(int hour, int minute) {                                                //this method converts the time into 12hr format and assigns am or pm
        String time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
            //picked_minute=minute;
        } else {
            formattedMinute = "" + minute;
            //picked_minute=minute;
        }
        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
            picked_am_pm="AM";
           // picked_hour=12;

        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
            picked_am_pm="AM";
            //picked_hour=hour;
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
            picked_am_pm="PM";
            //picked_hour=hour;
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
            picked_am_pm="PM";
            //picked_hour=temp;
        }

        return time;


    }
    String getDateInFormat(String date, String time) {

        return date + TIME_FORMATTER + time +":00";
    }


    private void setAlarm(Context context, String text, String date, String time, int requestCode) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);                   //assigning alarm manager object to set alarm
        Intent intent = new Intent(getContext(), AlarmBroadcast.class);
        Bundle b= new Bundle();
        b.putString("event", text);
        b.putString("date", date);
        b.putString("time", time);
        b.putInt("requestCode",requestCode);
        intent.putExtras(b);

//        intent.putExtra("event", text);                                                       //sending data to alarm class to create channel and notification
//        intent.putExtra("date", date);
//        intent.putExtra("time", time);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
        String dateandtime = getDateInFormat(date,time);
        //String testDate = "2022-10-29"+'T'+timeToNotify+":00";
        System.out.println("Edit page event : "+text);
        System.out.println("Edit page datetime : "+dateandtime);
        DateFormat formatter = new SimpleDateFormat(DATA_FORMAT);

        try {
            Date date1 = formatter.parse(dateandtime);
            System.out.println(date1.getTime());
            am.setExact(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intentBack = new Intent(getContext(), BreaksPage.class); //this intent will be called once the setting alarm is complete
        Toast.makeText(getContext(), "Alarm for "+text, Toast.LENGTH_SHORT).show();
        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentBack);                                                                  //navigates from adding reminder activity to mainactivity
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditBreaksPage.EditBreaksPageListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Implement EditBreaksPageListener");
        }
    }

    public interface  EditBreaksPageListener {
        void updateBreaksData(String title,String date,String time,int i,int requestCode,String break_ID,int isAlertOn);

        void deleteBreaksData();
    }
}




