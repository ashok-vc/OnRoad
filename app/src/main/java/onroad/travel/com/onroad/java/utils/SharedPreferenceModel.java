package onroad.travel.com.onroad.java.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cbhpl on 24/6/16.
 */
public class SharedPreferenceModel {
    String MyPREFERENCES="ONROADPREFERENCES";
    SharedPreferences sharedpreferences;
    public SharedPreferenceModel(Context con)
    {
        sharedpreferences = con.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }



    public void insertData(String key,boolean value)
    {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }



    public boolean retreiveData(String key,boolean defvalue)
    {
        return sharedpreferences.getBoolean(key,defvalue);
    }
}
