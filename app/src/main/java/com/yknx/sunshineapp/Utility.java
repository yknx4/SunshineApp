package com.yknx.sunshineapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.yknx.sunshineapp.data.WeatherContract;

import java.text.SimpleDateFormat;
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
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }

    static String formatDate(String dateString) {
        //TODO: FIX!!!!
        SimpleDateFormat sdf = new SimpleDateFormat();
        Date date = WeatherContract.getDateFromDb(dateString);
        String res = sdf.format(date);
        return res;

    }

}