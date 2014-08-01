package com.yknx.sunshineapp.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yknx on 31/07/2014.
 */
public class WeatherContract {
    public  static  final  String CONTENT_AUTHORITY = "com.yknx.sunshineapp";
            public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public  static  final  String PATH_WEATHER = "weather";
    public  static  final  String PATH_LOCATION = "location";

    public static Date getDateFromDb(String dateString) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat();
        try {
            return dbDateFormat.parse(dateString);
        }catch (ParseException e){
            e.printStackTrace();
            return null;
        }

    }


    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
// e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south). Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationPostalCode,String locationCountryCode) {
            return CONTENT_URI.buildUpon().appendPath(locationPostalCode+","+locationCountryCode).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationPostalCode,String locationCountryCode, String startDate) {
            return CONTENT_URI.buildUpon().appendPath(locationPostalCode+","+locationCountryCode)
                    .appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationPostalCode,String locationCountryCode, String date) {
            return CONTENT_URI.buildUpon().appendPath(locationPostalCode+","+locationCountryCode).appendPath(date).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class LocationEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String TABLE_NAME = "locations";


        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_LOCATION_LONG = "location_longitude";
        public static final String COLUMN_LOCATION_LAT = "location_latitude";
        public static final String COLUMN_LOCATION_POSTALCODE = "location_postalcode";
        public static final String COLUMN_LOCATION_COUNTRYCODE = "location_countrycode";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }





    }
    public static final String DATE_FORMAT ="yyyy/MMdd";
    public static String getDbDateString(Date date){
// Because the API returns a unix timestamp (measured in seconds),
// it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }
}