package com.yknx.sunshineapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.yknx.sunshineapp.data.WeatherContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yknx on 01/08/2014.
 */
public class Utility {
    public static Pair<String,String> getPreferredLocation(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String postalCode =prefs.getString(context.getString(R.string.pref_key_postalcode),
                context.getString(R.string.pref_default_postalcode));
        String countryCode =prefs.getString(context.getString(R.string.pref_key_country),
                context.getString(R.string.pref_default_country));

        return Pair.create(postalCode,countryCode);
    }

    static ContentValues createLocationValues(String locationPostalCode, String locationCountry, String cityName, double latitude, double longitude) {

        ContentValues testValues = new ContentValues();
        testValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_POSTALCODE, locationPostalCode);
        testValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_COUNTRYCODE, locationCountry);
        testValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
        testValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_LAT, latitude);
        testValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_LONG, longitude);

        return testValues;
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_tunits),
                context.getString(R.string.pref_default_tunits))
                        .equals(context.getString(R.string.pref_default_tunits));
    }

    static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        String res,grades;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
            grades = "°C";
        } else {
            temp = temperature;
            grades = "°F";
        }
        res = String.format("%.0f ", temp);
        return res+grades;
    }

    static String formatDate(String dateString) {
        //TODO: FIX!!!!
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d");
        String res;
        Date date = WeatherContract.getDateFromDb(dateString);


        int days = daysAfterBeforeToday(date);
        switch (days){
            default:
                res = sdf.format(date);
                break;
            case -1:
                res = "Yesterday";
                break;
            case 0:
                res = "Today";
                break;
            case  1:
                res = "Tomorrow";
                break;

        }


        return res;

    }

    static  int daysAfterBeforeToday(Date to){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);

       // Date today = c.getTime();

        long todayInMillis = c.getTimeInMillis();

        c.setTime(to);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);

        long toInMillis = c.getTimeInMillis();

        long days = toInMillis-todayInMillis;

        long total = days / 86400000;
        int result = (int) total;


        return  result;
    }

}