package com.yknx.sunshineapp.test;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Set;

import com.yknx.sunshineapp.data.WeatherContract.LocationEntry;
import com.yknx.sunshineapp.data.WeatherContract.WeatherEntry;
import com.yknx.sunshineapp.data.WeatherDbHelper;

/**
 * Created by Yknx on 31/07/2014.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }
    public  final String testCityName = "North Pole";
    public  void testInsertReadDb(){
        // Test com.yknx.sunshineapp.data we're going to insert into the DB to see if it works.



        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = createNorthPoleLocationValues();
        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME,null,values);

        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG,"New Row id: "+locationRowId);

        // Data's inserted. IN THEORY. Now pull some out to stare at it and verify it made
// the round trip.
// Specify which columns you want.
        String[] columns = {
                LocationEntry._ID,
                LocationEntry.COLUMN_LOCATION_POSTALCODE,
                LocationEntry.COLUMN_LOCATION_COUNTRYCODE,
                LocationEntry.COLUMN_CITY_NAME,
                LocationEntry.COLUMN_LOCATION_LAT,
                LocationEntry.COLUMN_LOCATION_LONG
        };
// A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME, // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursorAgainstContentValues(cursor, values);

        ContentValues weatherValues = createWeatherValues(locationRowId);
        long weatherRowId;
        weatherRowId = db.insert(WeatherEntry.TABLE_NAME,null,weatherValues);

        assertTrue(weatherRowId != -1);
        Log.d(LOG_TAG,"New Row id: "+weatherRowId);

        // Data's inserted. IN THEORY. Now pull some out to stare at it and verify it made
// the round trip.
// Specify which columns you want.
        String[] columnsW = {
                WeatherEntry._ID,
                WeatherEntry.COLUMN_LOC_KEY,
                WeatherEntry.COLUMN_DATETEXT,
                WeatherEntry.COLUMN_DEGREES,
                WeatherEntry.COLUMN_HUMIDITY,
                WeatherEntry.COLUMN_PRESSURE,
                WeatherEntry.COLUMN_MAX_TEMP,
                WeatherEntry.COLUMN_MIN_TEMP,
                WeatherEntry.COLUMN_SHORT_DESC,
                WeatherEntry.COLUMN_WIND_SPEED,
                WeatherEntry.COLUMN_WEATHER_ID,
        };
// A cursor is your primary interface to the query results.
        Cursor cursorW = db.query(
                WeatherEntry.TABLE_NAME, // Table to Query
                columnsW,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursorAgainstContentValues(cursorW, weatherValues);


        dbHelper.close();
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