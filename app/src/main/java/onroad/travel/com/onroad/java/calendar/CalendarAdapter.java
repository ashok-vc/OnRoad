package onroad.travel.com.onroad.java.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import onroad.travel.com.onroad.R;


public class CalendarAdapter extends BaseAdapter {
    private Context context;

    private java.util.Calendar month;
    public GregorianCalendar pmonth;
    /**
     * calendar instance for previous month for getting complete view
     */
    public GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    int firstDay;
    int maxWeeknumber;
    int maxP;
    int calMaxP;
    int lastWeekDay;
    int leftDays;
    int mnthlength;
    String itemvalue, curentDateString;
    DateFormat df;

    private ArrayList<String> items;
    public static List<String> day_string;
    private View previousView;
    public ArrayList<CalendarCollection>  date_collection_arr;
    public static TextView dayView;

    public CalendarAdapter(Context context, GregorianCalendar monthCalendar,ArrayList<CalendarCollection> date_collection_arr) {
        this.date_collection_arr=date_collection_arr;
        CalendarAdapter.day_string = new ArrayList<String>();
        Locale.setDefault(Locale.US);
        month = monthCalendar;
        selectedDate = (GregorianCalendar) monthCalendar.clone();
        this.context = context;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);

        this.items = new ArrayList<String>();
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();
    }

    public void setItems(ArrayList<String> items) {
        for (int i = 0; i != items.size(); i++) {
            if (items.get(i).length() == 1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.items = items;
    }

    public int getCount() {
        return day_string.size();
    }

    public Object getItem(int position) {
        return day_string.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        //TextView dayView;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.cal_item, null);
        }

        dayView = (TextView) v.findViewById(R.id.date);
        //dayView.setTypeface(Constants.font_light);

        String[] separatedTime = day_string.get(position).split("-");

        String gridvalue = separatedTime[2].replaceFirst("^0*", "");
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            dayView.setTextColor(context.getResources().getColor(R.color.verySleekTextColor));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            dayView.setTextColor(context.getResources().getColor(R.color.verySleekTextColor));
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            dayView.setTextColor(context.getResources().getColor(R.color.darkGray));
        }

        // to display the current date
        if (day_string.get(position).equals(curentDateString)) {
            //v.setBackgroundResource(R.drawable.rounded_current_calender_item);
            v.setBackgroundColor(Color.WHITE);
            dayView.setTextColor(context.getResources().getColor(R.color.red_color_primary));
        } else {
            v.setBackgroundColor(Color.WHITE);
        }

        dayView.setText(gridvalue);

        // create date string for comparison
        String date = day_string.get(position);
        if (date.length() == 1) {
            date = "0" + date;
        }

        String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        setEventView(v, position, dayView);

        return v;
    }

    public void setEventView(View v,int pos,TextView txt) {
        int len=CalendarCollection.date_collection_arr.size();
        if(len>0){
            for (int i = 0; i < len; i++) {
                CalendarCollection cal_obj=CalendarCollection.date_collection_arr.get(i);
                String date=cal_obj.date;
                int len1=day_string.size();
                if (len1>pos) {
                    if (day_string.get(pos).equals(date)) {
                        v.setBackgroundResource(R.drawable.rounded_calender_item);
                        txt.setTextColor(Color.WHITE);
                    }
                }
            }
        }
    }

//    public View setSelected(View view) {
//        view.setBackgroundResource(R.drawable.rounded_calender_item);
//        TextView dayView = (TextView) view.findViewById(R.id.date);
//        dayView.setTextColor(Color.WHITE);
//        dayView.setTypeface(Constants.font_light, Typeface.BOLD);
//        return view;
//    }

    public View removeSelected(View view,String selectedData) {
        view.setBackgroundColor(Color.WHITE);
        TextView dayView = (TextView) view.findViewById(R.id.date);
        if(checkForCurrentDateValidation(selectedData)) {
            dayView.setTextColor(context.getResources().getColor(R.color.red_color_primary));
        } else {
            dayView.setTextColor(context.getResources().getColor(R.color.darkGray));
        }
        return view;
    }

    public View removePreviousMonthSelectedDate(View view) {
        view.setBackgroundColor(Color.WHITE);
        TextView dayView = (TextView) view.findViewById(R.id.date);
        dayView.setTextColor(context.getResources().getColor(R.color.verySleekTextColor));
        return view;
    }

    public boolean checkForCurrentDateValidation(String dateStr){
        Boolean isValid = false;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        //current date
        Date today = new Date();
        String todayStr = formatter.format(today);
        if(dateStr.equals(todayStr)){
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

    public void refreshDays() {
        // clear items
        items.clear();
        day_string.clear();
        Locale.setDefault(Locale.US);
        pmonth = (GregorianCalendar) month.clone();
        // month start day. ie; sun, mon, etc
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
        /**
         * Calendar instance for getting a complete gridview including the three
         * month's (previous,current,next) dates.
         */
        pmonthmaxset = (GregorianCalendar) pmonth.clone();
        /**
         * setting the start date as previous month's required date.
         */
        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

        /**
         * filling calendar gridview.
         */
        for (int n = 0; n < mnthlength; n++) {
            itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            day_string.add(itemvalue);
        }
    }

    private int getMaxP() {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        return maxP;
    }

}