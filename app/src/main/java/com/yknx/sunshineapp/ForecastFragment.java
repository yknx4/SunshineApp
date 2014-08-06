package com.yknx.sunshineapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yknx.sunshineapp.data.WeatherContract;
import com.yknx.sunshineapp.data.WeatherContract.LocationEntry;
import com.yknx.sunshineapp.data.WeatherContract.WeatherEntry;
import com.yknx.sunshineapp.sync.SunshineSyncAdapter;

import java.util.Date;

/**
 * Created by Yknx on 30/07/2014.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // These indices are tied to FORECAST_COLUMNS. If FORECAST_COLUMNS changes, these
// must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_POSTALCODE = 5;
    public static final int COL_LOCATION_COUNTRY = 6;
    public static final int COL__WEATHER_WEATHERID = 6;
    private static final String LISTVIEW_POSITION = "lv_position";
    private static final int FORECAST_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
// Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
// In this case the id needs to be fully qualified with a table name, since
// the content provider joins the location & weather tables in the background
// (both have an _id column)
// On the one hand, that's annoying. On the other, you can search the weather table
// using the location set by the user, which is only in the Location table.
// So the convenience is worth it.
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            LocationEntry.COLUMN_LOCATION_POSTALCODE,
            LocationEntry.COLUMN_LOCATION_COUNTRYCODE,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_LOCATION_LAT,
            LocationEntry.COLUMN_LOCATION_LONG
    };
    final String LOG_TAG = ForecastFragment.class.getSimpleName();
    ForecastAdapter mForecastAdapter;
    private boolean mTwoPanel = false;
    private int mListviewPosition = 0;
    private Pair<String, String> mLocation;
    private ListView listView;

    public ForecastFragment() {
    }

    public void setTwoPanel(boolean TwoPanel) {
        mTwoPanel = TwoPanel;
        if (mForecastAdapter != null)
            mForecastAdapter.setUseTodayLayout(!mTwoPanel);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                updateWeather();
                break;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getView().getContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_showOnMap:
                showMap();
                break;
        }

        return true;

    }

    private void showMap() {
        Uri geoLocation ;
        if(locationUri!=null){
            geoLocation= Uri.parse(locationUri);
        }
        else {
            geoLocation= Uri.parse("geo:0,0?q=" + getLocationString());
        }

        Log.d(LOG_TAG, geoLocation.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListviewPosition != ListView.INVALID_POSITION) {
            outState.putInt(LISTVIEW_POSITION, mListviewPosition);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //ROOT VIEW
        if (savedInstanceState != null && savedInstanceState.containsKey(LISTVIEW_POSITION))
            mListviewPosition = savedInstanceState.getInt(LISTVIEW_POSITION);
        mForecastAdapter = new ForecastAdapter(
                getActivity(),
                null,
                0
        );
        mForecastAdapter.setUseTodayLayout(!mTwoPanel);


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mForecastAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    String date = cursor.getString(COL_WEATHER_DATE);
/*
                    //ForecastFragment f = (ForecastFragment)getActivity();

                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, date);

                    startActivity(intent);*/
                    ((CallbackListener) getActivity()).onItemSelected(date);
                    mListviewPosition = position;
                }
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(LISTVIEW_POSITION)) {
            mListviewPosition = savedInstanceState.getInt(LISTVIEW_POSITION);
        }
        return rootView;
    }

    private String getLocationString() {
        Pair<String, String> data = Utility.getPreferredLocation(getActivity());
        String postal_code, country, finalq;
        postal_code = data.first;
        country = data.second;
        finalq = postal_code + "," + country;
        Log.d(LOG_TAG, finalq);
        return finalq;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }

    private void updateWeather() {
       /* Intent updateWeather = new Intent(getActivity(), SunshineService.class);
        updateWeather.putExtra(SunshineService.EXTRA_LOCATIONSTRING,getLocationString());
        updateWeather.setAction(SunshineService.ACTION_UPDATEWEATHER);*/

//        Intent alarmIntent = new Intent(getActivity(),SunshineService.AlarmReceiver.class);
 //       alarmIntent.putExtra(SunshineService.EXTRA_LOCATIONSTRING,getLocationString());
  //      alarmIntent.setAction(SunshineService.ACTION_UPDATEWEATHER);

        //getActivity().startService(updateWeather);
    //    PendingIntent pi = PendingIntent.getBroadcast(getActivity(),0,alarmIntent,PendingIntent.FLAG_ONE_SHOT);
     //   AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
      //  am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+5000,pi);
        SunshineSyncAdapter.syncImmediately(getActivity());

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation.first, mLocation.second, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    private String locationUri=null;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount()==0){
            SunshineSyncAdapter.syncImmediately(getActivity());
        }
        if(data.moveToFirst()){
            double lat = data.getDouble(data.getColumnIndex(LocationEntry.COLUMN_LOCATION_LAT));
            double longi = data.getDouble(data.getColumnIndex(LocationEntry.COLUMN_LOCATION_LONG));
            locationUri = "geo:"+lat+","+longi;
        }
        mForecastAdapter.swapCursor(data);

        if (mListviewPosition != ListView.INVALID_POSITION) {
            listView.setSelection(mListviewPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface CallbackListener {
        /**
         * CallbackListener for when an item has been selected.
         */
        public void onItemSelected(String date);
    }
}
