package onroad.travel.com.onroad.java.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appacitive.core.AppacitiveObject;
import com.appacitive.core.apjson.APJSONException;
import com.appacitive.core.model.Callback;
import com.appacitive.core.model.PagedList;
import com.appacitive.core.query.AppacitiveQuery;
import com.appacitive.core.query.PropertyFilter;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import onroad.travel.com.onroad.R;
import onroad.travel.com.onroad.java.Constants;
import onroad.travel.com.onroad.java.models.Model;
import onroad.travel.com.onroad.java.utils.SharedPreferenceModel;

public class MainActivity extends Activity {
    SharedPreferenceModel smodel;
    Model model;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        model = new Model(MainActivity.this);
        setUserData();
        setTripsData();
        (findViewById(R.id.logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smodel.insertData(Constants.LoginStatus, false);
                LoginManager.getInstance().logOut();
                MainActivity.this.finish();
            }
        });

        (findViewById(R.id.create_trip)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCreateTrip = new Intent(MainActivity.this, CreateTrip.class);
                startActivity(goToCreateTrip);
            }
        });
    }

    private void setUserData() {
        Cursor cursor = model.getCompleteTable(Constants.tbl_usr);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String name = cursor.getString(cursor.getColumnIndex("firstname"));
                Constants.id = cursor.getString(cursor.getColumnIndex("appacitive_id"));
                String fb_id = cursor.getString(cursor.getColumnIndex("facebook_id"));
                ((TextView) findViewById(R.id.user_name)).setText(name);
                ((TextView) findViewById(R.id.fb_id)).setText(fb_id);
                Picasso.with(MainActivity.this)
                        .load("https://graph.facebook.com/" + fb_id + "/picture?type=large")
                        .placeholder(R.drawable.user_icon)
                        .into(((ImageView) findViewById(R.id.user_img)));

            }
        }

    }

    public void setTripsData() {
        // Build the query
        AppacitiveQuery appacitiveQuery = new AppacitiveQuery();
        Log.e("id", Constants.id);
        appacitiveQuery.filter = new PropertyFilter("admin_user_slug").match(Constants.id);


// Fire the query
        List<String> fields = null;
        AppacitiveObject.findInBackground("trip", appacitiveQuery, fields, new Callback<PagedList<AppacitiveObject>>() {
            @Override
            public void success(PagedList<AppacitiveObject> result) {
                try {
                    if (result.pagingInfo.totalRecords > 0) {
                        listView.setAdapter(new TripsAdapter(result.results));
                        ((TextView)findViewById(R.id.textView)).setText("Your Trips");
                    }
                    Log.e("TAG", String.format("%s own trips found.", result.results.get(0).getPropertyAsString("trip_destination")));
                } catch (Exception e) {
                    Log.e("err", e.toString());
                }
            }

            @Override
            public void failure(PagedList<AppacitiveObject> result, Exception e) {
            }
        });
    }

    class TripsAdapter extends BaseAdapter {
        List<AppacitiveObject> results;

        public TripsAdapter(List<AppacitiveObject> results) {
            this.results = results;
        }

        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflate=LayoutInflater.from(MainActivity.this);
            convertView=inflate.inflate(R.layout.trip_item,null);
            ((TextView)convertView.findViewById(R.id.name)).setText(results.get(0).getPropertyAsString("trip_name"));
            ((TextView)convertView.findViewById(R.id.src)).setText(results.get(0).getPropertyAsString("trip_source"));
            ((TextView)convertView.findViewById(R.id.dstn)).setText(results.get(0).getPropertyAsString("trip_destination"));
            ((TextView)convertView.findViewById(R.id.date)).setText(""+results.get(0).getPropertyAsDate("trip_departure_date"));
            return convertView;
        }
    }
}
