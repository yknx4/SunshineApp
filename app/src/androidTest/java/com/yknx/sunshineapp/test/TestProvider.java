package com.yknx.sunshineapp.test;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.yknx.sunshineapp.data.WeatherContract.LocationEntry;
import com.yknx.sunshineapp.data.WeatherContract.WeatherEntry;

import java.util.Set;

/**
 * Created by Yknx on 31/07/2014.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                WeatherEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void setUp() {
        deleteAllRecords();
    }

    public void testDeleteDb() throws Throwable {
        deleteAllRecords();

    }

    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "28001";
        String textCountry = "MX";
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocation(testLocation,textCountry));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation,textCountry, testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testUpdateLocation() {
// Create a new map of values, where column names are the keys
        ContentValues values = TestDb.createNorthPoleLocationValues();

        Uri locationUri = mContext.getContentResolver().
                insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

// Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(LocationEntry._ID, locationRowId);
        updatedValues.put(LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        int count = mContext.getContentResolver().update(
                LocationEntry.CONTENT_URI, updatedValues, LocationEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});

        assertEquals(count, 1);

// A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

       validateCursorAgainstContentValues(cursor, updatedValues);
    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }

    public  final String TEST_CITY_NAME = "North Pole";
    public  final  String TEST_CITY_POSTALCODE = "28001";
    public  final String TEST_CITY_COUNTRYCODE = "MX";
    public  final String TEST_DATE = "20141205";



    public  void testInsertReadProvider(){
        // Test com.yknx.sunshineapp.data we're going to insert into the DB to see if it works.





        ContentValues values = createNorthPoleLocationValues();
        long locationRowId;
        Uri locInsertUri = getContext().getContentResolver().insert(LocationEntry.CONTENT_URI, values);
        locationRowId = ContentUris.parseId(locInsertUri);
        //assertTrue(locationRowId != -1);
        //Log.d(LOG_TAG,"New Row id: "+locationRowId);


// A cursor is your primary interface to the query results.
       /* Cursor cursor = db.query(
                LocationEntry.TABLE_NAME, // Table to Query
                null, // All columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );*/
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI, // Table to Query
                null,   //All columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // columns to group by

        );
        validateCursorAgainstContentValues(cursor, values);
        cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // sort order
        );

        validateCursorAgainstContentValues(cursor, values);

        ContentValues weatherValues = createWeatherValues(locationRowId);

        Uri insertUri= mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);
      //  long weatherRowId = ContentUris.parseId(insertUri);

        //assertTrue(weatherRowId != -1);
        //Log.d(LOG_TAG,"New Row id: "+weatherRowId);


// A cursor is your primary interface to the query results.
        Cursor cursorWeather = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI, // Table to Query
                null,   //All columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // columns to group by

        );
        validateCursorAgainstContentValues(cursorWeather, weatherValues);
        cursorWeather.close();
        //TEST CURSOR WEATHER WITH JOIN
        cursorWeather = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocation(TEST_CITY_POSTALCODE,TEST_CITY_COUNTRYCODE), // Table to Query
                null,   //All columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // columns to group by

        );
        validateCursorAgainstContentValues(cursorWeather, weatherValues);
        cursorWeather.close();
        //TEST CURSOR WEATHER WITH JOIN
        cursorWeather = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(TEST_CITY_POSTALCODE,TEST_CITY_COUNTRYCODE,TEST_DATE), // Table to Query
                null,   //All columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // columns to group by

        );
        validateCursorAgainstContentValues(cursorWeather, weatherValues);

        cursorWeather.close();
        //TEST CURSOR WEATHER WITH JOIN
        cursorWeather = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithDate(TEST_CITY_POSTALCODE,TEST_CITY_COUNTRYCODE,TEST_DATE), // Table to Query
                null,   //All columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // columns to group by

        );
        validateCursorAgainstContentValues(cursorWeather, weatherValues);


    }
    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        return weatherValues;
    }

    static ContentValues createNorthPoleLocationValues() {
        String testLocationSetting = "28001";
        String testLocationCountry = "MX";
        String testCityName = "North Pole";
        double testLatitude = 64.7488;
        double testLongitude = -147.353;


// Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(LocationEntry.COLUMN_LOCATION_POSTALCODE, testLocationSetting);
        testValues.put(LocationEntry.COLUMN_LOCATION_COUNTRYCODE, testLocationCountry);
        testValues.put(LocationEntry.COLUMN_CITY_NAME, testCityName);
        testValues.put(LocationEntry.COLUMN_LOCATION_LAT, testLatitude);
        testValues.put(LocationEntry.COLUMN_LOCATION_LONG, testLongitude);

        return testValues;
    }
    static void validateCursorAgainstContentValues(
            Cursor valueCursor, ContentValues expectedValues) {

// If possible, move to the first row of the query results.
        assertTrue(valueCursor.moveToFirst());

// get the content values out of the cursor at the current position
        ContentValues resultValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(valueCursor, resultValues);

// make sure the values match the ones we put in
        validateContentValues(resultValues, expectedValues);
        valueCursor.close();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static void validateContentValues(ContentValues output, ContentValues input) {
        Set<String> inputKeys = input.keySet();
        for (String key : inputKeys) {
            assertTrue(output.containsKey(key));
            Log.d(LOG_TAG,"Row "+key+": "+input.getAsString(key)+" -> "+output.getAsString(key));
            assertTrue(output.getAsString(key).equals(input.getAsString(key)));
        }
}}