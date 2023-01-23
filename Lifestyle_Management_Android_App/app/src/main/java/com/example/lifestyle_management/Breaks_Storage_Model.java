package com.example.lifestyle_management;

public class Breaks_Storage_Model {
    private String break_ID;
    private String break_name;
    private String break_time;
    private String break_date;
    private int break_requestCode;
    private int isAlertOn;

    // Constructor
    public Breaks_Storage_Model(String ID , String break_name, String break_date, String break_time, int break_requestCode,int isAlertOn) {
        this.break_ID = ID;
        this.break_name = break_name;
        this.break_time = break_time;
        this.break_date = break_date;
        this.break_requestCode = break_requestCode;
        this.isAlertOn = isAlertOn;
    }

    public int isAlertOn() {
        return isAlertOn;
    }

    public void setAlertOn(int alertOn) {
        this.isAlertOn = alertOn;
    }

    public String getBreak_ID() {
        return break_ID;
    }

    public void setBreak_ID(String break_ID) {
        this.break_ID = break_ID;
    }

    // Getter and Setter
    public String getBreak_name() {
        return break_name;
    }

    public void setBreak_name(String break_name) {
        this.break_name = break_name;
    }

    public String getBreak_time() {
        return break_time;
    }

    public void setBreak_time(String break_time ) {
        this.break_time = break_time;
    }
    public String getBreak_date() {
        return break_date;
    }
    public void setBreak_date(String break_date) {
        this.break_date = break_date;
    }

    public int getBreak_requestCode() { return break_requestCode;}
    public void setBreak_requestCode(int break_requestCode) {this.break_requestCode = break_requestCode;}
}
