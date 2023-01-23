package com.example.lifestyle_management;

public class Task_Storage_Model {

    private String task_name;
    private String task_time;
    private String task_date;

    // Constructor
    public Task_Storage_Model(String task_name, String task_time, String task_date) {
        this.task_name = task_name;
        this.task_time = task_time;
        this.task_date = task_date;
    }

    // Getter and Setter
    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_time() {
        return task_time;
    }

    public void setTask_time(int course_rating) {
        this.task_time = task_time;
    }
    public String getTask_date() {
        return task_date;
    }
    public void setTask_date(String task_date) {
        this.task_date = task_date;
    }
}
