package onroad.travel.com.onroad.java.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;



import onroad.travel.com.onroad.java.Constants;


/**
 * Created by cbhpl on 13/8/16.
 */

public class Model extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "OnRoad.db";

    public Model(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + Constants.tbl_usr +
                        " (id integer primary key, appacitive_id text, username text,facebook_id text,user_dp text,user_country text, user_current_city text,no_free_trips text,user_slug text,location text,email text,firstname text,lastname text,birthdate text,phone text,password text, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)"
        );



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public boolean checkIfExists(String TableName, String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TableName + " where " + key + " = '" + value + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

//    username text,facebook_id text,
//    user_dp text,user_country text, user_current_city text,no_free_trips text,
//    user_slug text,location text,email text,firstname text,lastname text,birthdate text,phone text,
//    password text

    public void createUser(String appacitive_id,String username,String facebook_id, String user_dp,String user_country, String user_current_city,
                           String no_free_trips, String location, String email, String firstname,
                           String lastname, String birthdate, String phone,String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("appacitive_id", appacitive_id);
        contentValues.put("username", username);
        contentValues.put("facebook_id", facebook_id);
        contentValues.put("user_dp", user_dp);
        contentValues.put("user_country", user_country);
        contentValues.put("user_current_city", user_current_city);
        contentValues.put("no_free_trips", no_free_trips);
        contentValues.put("user_slug", appacitive_id);
        contentValues.put("location", location);
        contentValues.put("email", email);
        contentValues.put("firstname", firstname);
        contentValues.put("lastname", lastname);
        contentValues.put("birthdate", birthdate);
        contentValues.put("phone", phone);
        contentValues.put("password", password);
        db.insert(Constants.tbl_usr, null, contentValues);

    }



    public boolean commonUpdate(String tableName, ArrayList<String> keys, ArrayList<String> values, String searchKey, String searchValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < keys.size(); i++) {
            contentValues.put("" + keys.get(i), "" + values.get(i));
        }
        db.update(tableName, contentValues, searchKey + " = ? ", new String[]{searchValue});
        return true;
    }

    public void deleteRow(String tableName, String searchKey, String searchValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName,
                searchKey + " = ? ",
                new String[]{searchValue});
    }

    public Cursor getCompleteTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + tableName, null);
    }

    public Cursor getData(String tableName, String searchKey, String searchValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + tableName + " where " + searchKey + "= '" + searchValue + "'", null);
    }

}
