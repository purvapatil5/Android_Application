package com.example.lifestyle_management;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "USER_RECORD";

    // Table to store user data
    private static final String TABLE_NAME = "USER_DATA";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "USERNAME";
    private static final String COL_3 = "EMAIL";
    private static final String COL_4 = "PASSWORD";

    // Table to Breaks data
    private static final String BREAKS_TABLE = "BREAKS_TABLE";
    private static final String BREAK_ID = "BREAK_ID";
    private static final String BREAK_NAME = "BREAK_NAME";
    private static final String BREAK_DATE = "BREAK_DATE";
    private static final String BREAK_TIME = "BREAK_TIME";
    private static final String BREAK_REQUEST_CODE = "BREAK_REQUEST_CODE";
    private static final String BREAK_ALERT_ON = "BREAK_ALERT_ON";
    private static final String USER_EMAIL = "USER_EMAIL";
    private final Context context;
    private SharedPreferences sp;


    public DatabaseHelper(@Nullable Context context) {

        super(context, DATABASE_NAME , null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , USERNAME TEXT , EMAIL TEXT , PASSWORD TEXT )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + BREAKS_TABLE);
        onCreate(db);
    }

    public boolean registerUser(String username , String email , String password){

        SQLiteDatabase db = this.getWritableDatabase();
        password = md5(password);
        ContentValues values = new ContentValues();
        values.put(COL_2 , username);
        values.put(COL_3 , email);
        values.put(COL_4 , password);
        Cursor c = db.rawQuery("SELECT * FROM USER_DATA where email= '"+email +"'", null);
            if(c.getCount()>0)
            {
                return false;
            }
            else{
                long result = db.insert(TABLE_NAME , null , values);
                if(result == -1)
                    return false;
                else
                    return true;
            }
    }

    public boolean checkUser(String email , String password){

        SQLiteDatabase db = this.getWritableDatabase();
        password = md5(password);
        String [] columns = { COL_1 };
        String selection = COL_3 + "=?" + " and " + COL_4 + "=?";
        String [] selectionargs = { email , password};
        Cursor cursor = db.query(TABLE_NAME , columns , selection ,selectionargs , null , null , null);
        int count = cursor.getCount();
        db.close();
        cursor.close();
        if (count > 0)
            return true;
        else
            return false;

    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    public boolean doesTableExist(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'",null);

        if (cursor.getCount() > 0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public void createBreaksTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS "+BREAKS_TABLE + "(BREAK_ID INTEGER PRIMARY KEY AUTOINCREMENT , BREAK_NAME TEXT , BREAK_DATE TEXT , BREAK_TIME TEXT, BREAK_REQUEST_CODE INTEGER,BREAK_ALERT_ON BOOLEAN,USER_EMAIL TEXT)");
    }

    public void addBreak(String breakName, String breakDate, String breakTime, int requestCode,int isAlertOn, String email){
        String  userEmail = email;
        if(userEmail == null){
            sp = context.getSharedPreferences("login" , MODE_PRIVATE);
            userEmail = sp.getString("Email",null);
        }
        if(userEmail == null) Toast.makeText(context,"error while adding break", Toast.LENGTH_SHORT).show();;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BREAK_NAME,breakName);
        values.put(BREAK_DATE,breakDate);
        values.put(BREAK_TIME,breakTime);
        values.put(BREAK_REQUEST_CODE,requestCode);
        values.put(BREAK_ALERT_ON,isAlertOn);
        values.put(USER_EMAIL, userEmail);

        long result = db.insert(BREAKS_TABLE, null, values);

        if(result == -1){
            Toast.makeText(context,"error while adding break", Toast.LENGTH_SHORT).show();
        }
    }

    // fetches  breaks data for each user.
    public Cursor getALlBreaksData(){
        sp = context.getSharedPreferences("login" , MODE_PRIVATE);
        String  userEmail = sp.getString("Email",null);

        String query = "SELECT * FROM BREAKS_TABLE WHERE USER_EMAIL='" + userEmail+"'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if(db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }
    
     public Cursor getALlBreaksDataIfAlertIsOn(){
        String query = "SELECT * FROM BREAKS_TABLE WHERE BREAK_ALERT_ON = 1";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if(db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }
    
    public void updateBreak(String breakID, String breakName, String breakDate, String breakTime, int requestCode, int isAlertOn){
        System.out.println("BreakDateUpdation " + breakName + isAlertOn);
        sp = context.getSharedPreferences("login" , MODE_PRIVATE);
        String  userEmail = sp.getString("Email",null);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BREAK_NAME,breakName);
        values.put(BREAK_DATE,breakDate);
        values.put(BREAK_TIME,breakTime);
        values.put(BREAK_REQUEST_CODE,requestCode);
        values.put(BREAK_ALERT_ON,isAlertOn);
        values.put(USER_EMAIL, userEmail);

        long result = db.update(BREAKS_TABLE,values,"BREAK_ID =?", new String[]{breakID});
        if(result == -1){
            Toast.makeText(context,"error while updating break", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Break updated successfully", Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteBreak(String breakID){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(BREAKS_TABLE,"BREAK_ID =?", new String[]{breakID});
        if(result == -1){
            Toast.makeText(context,"error while deleting break", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Break deleted successfully", Toast.LENGTH_SHORT).show();
        }
    }
    // fetches entire breaks data
    public Cursor getBreaksInformation(){
//        sp = context.getSharedPreferences("login" , MODE_PRIVATE);
//        String  userEmail = sp.getString("Email",null);

        String query = "SELECT * FROM BREAKS_TABLE";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if(db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;

    }


}
