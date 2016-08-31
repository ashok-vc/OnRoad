package onroad.travel.com.onroad.java.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appacitive.core.AppacitiveObject;
import com.appacitive.core.model.Callback;

import java.util.ArrayList;
import java.util.Date;

import onroad.travel.com.onroad.R;
import onroad.travel.com.onroad.java.CommonMethods;
import onroad.travel.com.onroad.java.Constants;

public class CreateTrip extends Activity {
    EditText trip_name_et, trip_category_et, departure_place_et, destination_place_et, expected_budget_et;
    CheckBox open_trip_cb, alcohol_cb, smoking_cb;
    RadioGroup rd_grp_mode;
    Button start_date_btn, end_date_btn;
    TextView bottom_btn_proceed;

    String trip_name, trip_category, trip_departure_place, trip_destination_place, trip_expected_budget, trip_mode = "", trip_start_date, trip_end_date;
    boolean trip_is_open, trip_is_alcohol,
            trip_is_smoking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        init();
        setListeners();
    }

    public void init() {
        trip_name_et = (EditText) findViewById(R.id.trip_name_et);
        trip_category_et = (EditText) findViewById(R.id.trip_category_et);
        departure_place_et = (EditText) findViewById(R.id.departure_place_et);
        destination_place_et = (EditText) findViewById(R.id.destination_place_et);
        expected_budget_et = (EditText) findViewById(R.id.expected_budget_et);

        open_trip_cb = (CheckBox) findViewById(R.id.open_trip_cb);
        alcohol_cb = (CheckBox) findViewById(R.id.alcohol_cb);
        smoking_cb = (CheckBox) findViewById(R.id.smoking_cb);

        rd_grp_mode = (RadioGroup) findViewById(R.id.rd_grp_mode);

        start_date_btn = (Button) findViewById(R.id.start_date_btn);
        end_date_btn = (Button) findViewById(R.id.end_date_btn);

        bottom_btn_proceed = (TextView) findViewById(R.id.bottom_btn_proceed);
    }

    public void setListeners() {
        bottom_btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trip_name = trip_name_et.getText().toString();
                trip_category = trip_category_et.getText().toString();
                trip_departure_place = departure_place_et.getText().toString();
                trip_destination_place = destination_place_et.getText().toString();
                trip_expected_budget = expected_budget_et.getText().toString();
                trip_is_open = open_trip_cb.isChecked();
                trip_is_alcohol = alcohol_cb.isChecked();
                trip_is_smoking = smoking_cb.isChecked();
                trip_start_date = start_date_btn.getText().toString();
                trip_end_date = end_date_btn.getText().toString();
                switch (rd_grp_mode.getCheckedRadioButtonId()) {
                    case R.id.mode_bike_rb: {
                        trip_mode = "bike";
                        break;
                    }
                    case R.id.mode_car_rb: {
                        trip_mode = "car";
                        break;
                    }
                    case R.id.mode_bus_rb: {
                        trip_mode = "bus";
                        break;
                    }
                    case R.id.mode_flight_rb: {
                        trip_mode = "flight";
                        break;
                    }
                    case R.id.mode_train_rb: {
                        trip_mode = "train";
                        break;
                    }


                }

                Log.e(rd_grp_mode.getCheckedRadioButtonId() + "", R.id.mode_car_rb + "");

                addToTripTable(trip_name, trip_category, trip_departure_place, trip_destination_place, trip_expected_budget, trip_is_open, trip_is_alcohol,
                        trip_is_smoking, trip_mode, trip_start_date, trip_end_date);

            }
        });
    }

    public void addToTripTable(String trip_name, String trip_category, String trip_departure_place, String trip_destination_place, String trip_expected_budget, boolean trip_is_open, boolean trip_is_alcohol,
                               boolean trip_is_smoking, String trip_mode, String trip_start_date, String trip_end_date) {
        //    Create a new AppacitiveObject to store a post.
        AppacitiveObject post = new AppacitiveObject("trip");

//    Set two string/text properties, 'title' & ''text.
        post.setStringProperty("trip_slug", CommonMethods.createSlug(System.currentTimeMillis()));
        post.setStringProperty("trip_invite_slug", CommonMethods.createSlug(System.currentTimeMillis()));
        post.setStringProperty("admin_user_slug", Constants.id);
        post.setStringProperty("trip_status", "open");
        post.setStringProperty("trip_name", trip_name);
        post.setDateProperty("trip_departure_date", new Date(System.currentTimeMillis()));
        post.setDateProperty("trip_arrival_date", new Date(System.currentTimeMillis()));
        post.setStringProperty("trip_source", trip_departure_place);
        post.setStringProperty("trip_destination", trip_destination_place);
        post.setStringProperty("trip_expected_budget", trip_expected_budget);
        post.setBoolProperty("is_public", trip_is_open);
        post.setBoolProperty("is_drinking_allowed", trip_is_alcohol);
        post.setBoolProperty("is_smoking_allowed", trip_is_smoking);
        post.setStringProperty("trip_category", trip_category);
        Log.e("trip_mode", trip_mode);
        post.setStringProperty("medium_of_transport", trip_mode);

//    Create the object on Appacitive.
        post.createInBackground(new Callback<AppacitiveObject>() {
            @Override
            public void success(AppacitiveObject result) {
                //  'result' holds the newly created 'post' object.
                Log.e("result", result.getId() + "");

                //    Create a new AppacitiveObject to store a post.
                AppacitiveObject post = new AppacitiveObject("trip_members");

//    Set two string/text properties, 'title' & ''text.
                post.setStringProperty("trip_id", result.getId() + "");
                post.setStringProperty("trip_member_slug", Constants.id);

//    Create the object on Appacitive.
                post.createInBackground(new Callback<AppacitiveObject>() {
                    @Override
                    public void success(AppacitiveObject result) {
                        //  'result' holds the newly created 'post' object.
                        Log.e("result", result.getId() + "");
                        CreateTrip.this.finish();
                    }

                    @Override
                    public void failure(AppacitiveObject result, Exception e) {
                        //  in case of error, e holds the error details.
                        Log.e("fail", e.toString());
                    }
                });

            }

            @Override
            public void failure(AppacitiveObject result, Exception e) {
                //  in case of error, e holds the error details.
                Log.e("fail", e.toString());
            }
        });
    }
}
