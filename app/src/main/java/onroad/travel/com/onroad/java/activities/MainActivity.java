package onroad.travel.com.onroad.java.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import onroad.travel.com.onroad.R;
import onroad.travel.com.onroad.java.Constants;
import onroad.travel.com.onroad.java.models.Model;
import onroad.travel.com.onroad.java.utils.SharedPreferenceModel;

public class MainActivity extends Activity {
SharedPreferenceModel smodel;
    Model model;
    Button create_trip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        model=new Model(MainActivity.this);
        setUserData();

        (findViewById(R.id.logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smodel.insertData(Constants.LoginStatus,false);
                LoginManager.getInstance().logOut();
                MainActivity.this.finish();
            }
        });

        (findViewById(R.id.create_trip)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCreateTrip=new Intent(MainActivity.this,CreateTrip.class);
                startActivity(goToCreateTrip);
            }
        });
    }

    private void setUserData()
    {
        Cursor cursor=model.getCompleteTable(Constants.tbl_usr);
        if (cursor!=null) {
            if (cursor.getCount()>0) {
                cursor.moveToFirst();
                String name=cursor.getString(cursor.getColumnIndex("firstname"));
                String fb_id=cursor.getString(cursor.getColumnIndex("facebook_id"));
                ((TextView)findViewById(R.id.user_name)).setText(name);
                ((TextView)findViewById(R.id.fb_id)).setText(fb_id);
                Picasso.with(MainActivity.this)
                        .load("https://graph.facebook.com/" + fb_id + "/picture?type=large")
                        .placeholder(R.drawable.user_icon)
                        .into(((ImageView) findViewById(R.id.user_img)));

            }
        }

    }
}
