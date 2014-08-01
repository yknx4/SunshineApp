package com.yknx.sunshineapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Yknx on 31/07/2014.
 */
public class WeatherProvider extends ContentProvider{
    private  static final String LOG_TAG = WeatherProvider.class.getSimpleName();

    private static final int WEATHER = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    private static final int LOCATION = 300;
    private static final int LOCATION_ID = 301;
    private static final String URI_AUTORITHY = WeatherContract.CONTENT_AUTHORITY;
    private UriMatcher mUriMatcher = buildUriMatcher();
    private WeatherDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher(){

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(URI_AUTORITHY,WeatherContract.PATH_WEATHER,WEATHER);
        matcher.addURI(URI_AUTORITHY,WeatherContract.PATH_WEATHER+"/*",WEATHER_WITH_LOCATION);
        matcher.addURI(URI_AUTORITHY,WeatherContract.PATH_WEATHER+"/*/*",WEATHER_WITH_LOCATION_AND_DATE);
        matcher.addURI(URI_AUTORITHY,WeatherContract.PATH_LOCATION,LOCATION);
        matcher.addURI(URI_AUTORITHY,WeatherContract.PATH_LOCATION+"/#",LOCATION_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (mUriMatcher.match(uri)) {
            // "weather/*/*"
            case WEATHER_WITH_LOCATION_AND_DATE:
            {
                retCursor = getWeatherByLocationSettingWithDate(uri, projection, sortOrder);
                if(true);
                break;
            }
            // "weather/*"
            case WEATHER_WITH_LOCATION: {
                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                if(true);
                break;
            }
            // "weather"
            case WEATHER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location/*"
            case LOCATION_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        WeatherContract.LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location"
            case LOCATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match){
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION:
                return  WeatherContract.WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return  WeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return  WeatherContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri=null;
        long _id;
        final int match = mUriMatcher.match(uri);
        switch (match){
            case WEATHER:
                 _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,contentValues);
                if(_id>0){

                    returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
                }
                else throw new SQLException("Failed to insert row into "+uri);
                break;
            case LOCATION:
                 _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,contentValues);
                if(_id>0){

                    returnUri = WeatherContract.LocationEntry.buildLocationUri(_id);
                }
                else throw new SQLException("Failed to insert row into "+uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String strFilter,locQuery;
        Uri toNotify=null;
        long _id=0;
        int affected;
        final int match = mUriMatcher.match(uri);
        switch (match){
            case WEATHER_WITH_LOCATION:
                String locSettings = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
                String[] locArgs = locSettings.split(",");
                locQuery = WeatherContract.LocationEntry.COLUMN_LOCATION_POSTALCODE + " = ? AND " +
                        WeatherContract.LocationEntry.COLUMN_LOCATION_COUNTRYCODE + "= ?";
                Cursor location = query(WeatherContract.LocationEntry.CONTENT_URI,null,locQuery,locArgs,null);
                strFilter = WeatherContract.WeatherEntry.COLUMN_LOC_KEY+" = "+location.getInt(location.getColumnIndex(WeatherContract.LocationEntry._ID));
                affected = db.delete(WeatherContract.WeatherEntry.TABLE_NAME, strFilter, null);
                if(affected>0){

                    toNotify = WeatherContract.WeatherEntry.CONTENT_URI;
                }
                else throw new SQLException("Failed to delete row "+uri);
                break;
            case WEATHER:
                affected = db.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
                if(affected>0){

                    toNotify = WeatherContract.WeatherEntry.CONTENT_URI;
                }
                else Log.w(LOG_TAG,"Failed to delete  " + uri);
                break;
            case LOCATION_ID:
                long locId = ContentUris.parseId(uri);
                locQuery = WeatherContract.LocationEntry._ID + " =  " + locId;

                affected = db.delete(WeatherContract.WeatherEntry.TABLE_NAME, locQuery, null);
                if(affected>0){

                    toNotify = WeatherContract.WeatherEntry.CONTENT_URI;
                }
                else throw new SQLException("Failed to delete row "+uri);
                break;
            case LOCATION:
                affected = db.delete(WeatherContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                if(affected>0){

                    toNotify = WeatherContract.LocationEntry.CONTENT_URI;
                }
                else Log.w(LOG_TAG,"Failed to delete  " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        if (toNotify!=null) getContext().getContentResolver().notifyChange(toNotify,null);
        return affected;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String strFilter,locQuery;
        Uri toNotify;
        long _id;
        int affected;
        final int match = mUriMatcher.match(uri);
        switch (match){
            case WEATHER_WITH_LOCATION:
                String locSettings = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
                String[] locArgs = locSettings.split(",");
                locQuery = WeatherContract.LocationEntry.COLUMN_LOCATION_POSTALCODE + " = ? AND " +
                        WeatherContract.LocationEntry.COLUMN_LOCATION_COUNTRYCODE + "= ?";
                Cursor location = query(WeatherContract.LocationEntry.CONTENT_URI,null,locQuery,locArgs,null);
                strFilter = WeatherContract.WeatherEntry.COLUMN_LOC_KEY+" = "+location.getInt(location.getColumnIndex(WeatherContract.LocationEntry._ID));
                affected = db.update(WeatherContract.WeatherEntry.TABLE_NAME,values,strFilter,null);
                if(affected>0){

                    toNotify = WeatherContract.WeatherEntry.CONTENT_URI;
                }
                else throw new SQLException("Failed to update row into "+uri);
                break;
            case LOCATION_ID:
                _id = ContentUris.parseId(uri);
                strFilter = WeatherContract.LocationEntry._ID+" = "+_id;
                affected = db.update(WeatherContract.LocationEntry.TABLE_NAME,values,strFilter,null);
                if(affected>0){

                    toNotify = WeatherContract.LocationEntry.CONTENT_URI;
                }
                else throw new SQLException("Failed to update row into "+uri);
                break;
            case WEATHER:
                affected = db.update(WeatherContract.WeatherEntry.TABLE_NAME,values,selection, selectionArgs);
                if(affected>0){

                    toNotify = WeatherContract.WeatherEntry.CONTENT_URI;
                }
                else throw new SQLException("Failed to update row into "+uri);
                break;
            case LOCATION:
                affected = db.update(WeatherContract.LocationEntry.TABLE_NAME,values,selection, selectionArgs);
                if(affected>0){

                    toNotify = WeatherContract.LocationEntry.CONTENT_URI;
                }
                else throw new SQLException("Failed to update row into "+uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(toNotify,null);
        return affected;
    }

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static{
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);
    }

    private static final String sLocationSettingSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_POSTALCODE + " = ? AND " +
                    WeatherContract.LocationEntry.COLUMN_LOCATION_COUNTRYCODE + "= ?";
    private static final String sLocationSettingWithStartDateSelection =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_POSTALCODE + " = ? AND " +
                    WeatherContract.LocationEntry.COLUMN_LOCATION_COUNTRYCODE + "= ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " >= ? ";
    private static final String sLocationSettingWithDaySelectionn =
            WeatherContract.LocationEntry.TABLE_NAME+
                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_POSTALCODE + " = ? AND " +
                    WeatherContract.LocationEntry.COLUMN_LOCATION_COUNTRYCODE + "= ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " = ? ";

    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] finalLocationArgs = locationSetting.split(",");

        String[] selectionArgs;
        String selection;

        if (startDate == null) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{finalLocationArgs[0],finalLocationArgs[1]};
        } else {
            selectionArgs = new String[]{finalLocationArgs[0],finalLocationArgs[1], startDate};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getWeatherByLocationSettingWithDate(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        String day = WeatherContract.WeatherEntry.getDateFromUri(uri);

        String[] finalLocationArgs = locationSetting.split(",");

        String[] selectionArgs;
        String selection;

        if (day == null) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{finalLocationArgs[0],finalLocationArgs[1]};
        } else {
            selectionArgs = new String[]{finalLocationArgs[0],finalLocationArgs[1], day};
            selection = sLocationSettingWithDaySelectionn;
        }

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


}
