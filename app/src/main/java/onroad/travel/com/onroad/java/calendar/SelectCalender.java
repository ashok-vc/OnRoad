package onroad.travel.com.onroad.java.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import onroad.travel.com.onroad.R;
import onroad.travel.com.onroad.java.interfaces.OnAsyncTaskCompleted;

public class SelectCalender extends Activity implements OnAsyncTaskCompleted {
    Activity act;
    public GregorianCalendar cal_month, cal_month_copy;
    private CalendarAdapter cal_adapter;
    public TextView tv_month;
    public TextView previous;

    ImageView back_btn;


    public SelectCalender() {
        act = SelectCalender.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_calender);
        act = SelectCalender.this;
//        setCancelable(true);
//        setCanceledOnTouchOutside(true);
        CalendarCollection.date_collection_arr = new ArrayList<CalendarCollection>();

        cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
        cal_month_copy = (GregorianCalendar) cal_month.clone();
        cal_adapter = new CalendarAdapter(act, cal_month, CalendarCollection.date_collection_arr);


        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));
        tv_month.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                displayPrevDateOption();
            }
        });

        previous = (TextView) findViewById(R.id.ib_prev);
        previous.setText("<");
        previous.setEnabled(false);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
            }
        });

        TextView next = (TextView) findViewById(R.id.Ib_next);
        next.setText(">");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();
            }
        });

        GridView gridview = (GridView) findViewById(R.id.gv_calendar);
        gridview.setAdapter(cal_adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String selectedGridDate = CalendarAdapter.day_string.get(position);
                if (checkForSelectedDateValidation(selectedGridDate)) {
                    if (checkForExistingData(selectedGridDate)) {
                        deleteEvents(selectedGridDate, position, parent, v);
                        Log.e("status", "data found and removed");
                    } else {
                        saveEvents(selectedGridDate, position, parent, v);
                        Log.e("status", "data is stored");
                    }
                    Log.e("arr", Arrays.toString(CalendarCollection.date_collection_arr.toArray()));
                } else {
//                    Utils.alertMsg(act, "Selected date should be greater than current date");
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onApiCallingCompleted(String response) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayPrevDateOption() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy");
        Date currentMonthYear = new Date();
        String currentMonthYearStr = formatter.format(currentMonthYear);
        String gotMonth = android.text.format.DateFormat.format("MMMM yyyy", cal_month).toString();
        if (gotMonth.equals(currentMonthYearStr)) {
            previous.setEnabled(false);
        } else {
            previous.setEnabled(true);
        }
    }

    public static JSONArray toJsonArray() {
        JSONArray arr = new JSONArray();
        try {
            int len = CalendarCollection.date_collection_arr.size();
            for (int i = 0; i <= len - 1; i++) {
                String date = CalendarCollection.date_collection_arr.get(i).date;
                String eventMessage = CalendarCollection.date_collection_arr.get(i).event_message;
                JSONObject obj = new JSONObject();
                obj.put("date", date);
                //obj.put("event",eventMessage);
                arr.put(i, obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }

    public boolean checkForSelectedDateValidation(String dateStr) {
        Boolean isValid = false;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        //selected date
        long selectedDateInMillis = convertDateToMillisec(dateStr);

        //current date
        Date today = new Date();
        String todayStr = formatter.format(today);
        long currentDateInMillis = convertDateToMillisec(todayStr);

        if (selectedDateInMillis < currentDateInMillis) {
            isValid = false;
        } else {
            isValid = true;
        }

        return isValid;
    }

    public long convertDateToMillisec(String dateStr) {
        long selectedDateInMillis = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(dateStr);
            selectedDateInMillis = date.getTime();
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        return selectedDateInMillis;
    }

    public boolean checkForExistingData(String selectedGridDate) {
        boolean isExisting = false;
        int len = CalendarCollection.date_collection_arr.size();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                String date = CalendarCollection.date_collection_arr.get(i).date;
                if (date.equals(selectedGridDate)) {
                    CalendarCollection.date_collection_arr.remove(i);
                    int length = CalendarCollection.date_collection_arr.size();
                    if (length == 0) {
                        CalendarCollection.date_collection_arr.clear();
                    }
                    isExisting = true;
                    break;
                } else {
                    isExisting = false;
                }
            }
        } else {
            isExisting = false;
        }
        return isExisting;
    }

    public void saveEvents(String selectedGridDate, int pos, AdapterView<?> parent, View v) {
        String eventStr = "Events is added on " + selectedGridDate;
        CalendarCollection.date_collection_arr.clear();
        //((CalendarAdapter) parent.getAdapter()).setSelected(v);
        CalendarCollection.date_collection_arr.add(new CalendarCollection(selectedGridDate, eventStr));
        String[] separatedTime = selectedGridDate.split("-");
        String gridValueString = separatedTime[2].replaceFirst("^0*", "");
        int gridValue = Integer.parseInt(gridValueString);
        if ((gridValue > 10) && (pos < 8)) {
            setPreviousMonth();
        } else if ((gridValue < 7) && (pos > 28)) {
            setNextMonth();
        }
        refreshCalendar();
    }

    public void deleteEvents(String selectedGridDate, int pos, AdapterView<?> parent, View v) {
        String[] separatedTime = selectedGridDate.split("-");
        String gridValueString = separatedTime[2].replaceFirst("^0*", "");
        int gridValue = Integer.parseInt(gridValueString);
        if ((gridValue > 10) && (pos < 8)) {
            ((CalendarAdapter) parent.getAdapter()).removePreviousMonthSelectedDate(v);
        } else if ((gridValue < 7) && (pos > 28)) {
            ((CalendarAdapter) parent.getAdapter()).removePreviousMonthSelectedDate(v);
        } else {
            ((CalendarAdapter) parent.getAdapter()).removeSelected(v, selectedGridDate);
        }
    }

    protected void setNextMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1),
                    cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) + 1);
        }
    }

    protected void setPreviousMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1),
                    cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) - 1);
        }
    }

    public void refreshCalendar() {
        cal_adapter.refreshDays();
        cal_adapter.notifyDataSetChanged();
        tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));
    }



}
