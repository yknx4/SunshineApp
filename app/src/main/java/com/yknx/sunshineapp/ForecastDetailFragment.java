package com.yknx.sunshineapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yknx.sunshineapp.data.WeatherContract;

/**
 * Created by Yknx on 31/07/2014.
 */
public class ForecastDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final int DETAIL_LOADER = 0;
    private Pair<String,String> mLocation;

    // For the forecast view we're showing only a small subset of the stored data.
// Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
// In this case the id needs to be fully qualified with a table name, since
// the content provider joins the location & weather tables in the background
// (both have an _id column)
// On the one hand, that's annoying. On the other, you can search the weather table
// using the location set by the user, which is only in the Location table.
// So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.LocationEntry.COLUMN_LOCATION_POSTALCODE,
            WeatherContract.LocationEntry.COLUMN_LOCATION_COUNTRYCODE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };




    private final String LOG_TAG = ForecastDetailFragment.class.getSimpleName();
    private  String mForecastStr;
    private ShareActionProvider mShareActionProvider;
    public ForecastDetailFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = new ShareActionProvider(getView().getContext());
        MenuItemCompat.setActionProvider(item,mShareActionProvider);
        inflater.inflate(R.menu.detailfragment, menu);


        Log.d(LOG_TAG,item.toString());
        //mShareActionProvider = (ShareActionProvider) item.getActionProvider();



        if(mShareActionProvider!=null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else Log.e(LOG_TAG,"No Sharing Provider");


    }
    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,mForecastStr+" #SunshineApp");
        return shareIntent;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
            /*int id = item.getItemId();
            switch (id){
                case R.id.action_share:
                    Intent sharing = new Intent(Intent.ACTION_SEND);
                    sharing.putExtra(Intent.EXTRA_TEXT,getActivity().getIntent().getStringExtra(Intent.EXTRA_SHORTCUT_NAME)+" #SunshineApp");
                    sharing.setType("text/plain");
                    Log.d(LOG_TAG,"Sharing intent");
                    setShareIntent(sharing);

                    break;

            }*/

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        /*Intent originalMessage = getActivity().getIntent();
        TextView data = (TextView) rootView.findViewById(R.id.textview_forecastData);
        mForecastStr = originalMessage.getStringExtra(Intent.EXTRA_TEXT);
        data.setText(mForecastStr);*/
        Intent originalIntend = getActivity().getIntent();
        Bundle loaderArgs = new Bundle();
        loaderArgs.putString(WeatherContract.WeatherEntry.COLUMN_DATETEXT,originalIntend.getStringExtra(Intent.EXTRA_TEXT));
        getLoaderManager().initLoader(DETAIL_LOADER,loaderArgs,this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(args==null) {
            throw new UnsupportedOperationException("No arguments supplied");
        }
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String specificDate = args.getString(WeatherContract.WeatherEntry.COLUMN_DATETEXT);



        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                mLocation.first, mLocation.second, specificDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //mForecastAdapter.swapCursor(data);
        TextView date = (TextView) getView().findViewById(R.id.detail_date_textview);
        TextView forecast = (TextView) getView().findViewById(R.id.detail_forecast_textview);
        TextView highT = (TextView) getView().findViewById(R.id.detail_high_textview);
        TextView lowT = (TextView) getView().findViewById(R.id.detail_low_textview);
        TextView humidity = (TextView) getView().findViewById(R.id.detail_humidity_textview);
        TextView wind = (TextView) getView().findViewById(R.id.detail_wind_textview);
        TextView pressure = (TextView) getView().findViewById(R.id.detail_pressure_textview);
        ImageView weatherIcon = (ImageView) getView().findViewById(R.id.detail_weathericon);

        if (data.moveToFirst()) {
            date.setText(Utility.formatDate(data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT))));
            forecast.setText(data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)));
            highT.setText(Utility.formatTemperature(getActivity(),data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)),Utility.isMetric(getActivity())));
            lowT.setText(Utility.formatTemperature(getActivity(),data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)),Utility.isMetric(getActivity())));
            humidity.setText(getActivity().getString(R.string.format_humidity,data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY))));
            pressure.setText(getActivity().getString(R.string.format_pressure,data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE))));
            wind.setText(Utility.getFormattedWind(getActivity(),data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)),data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES))));
            weatherIcon.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID))));

        } else {
            Log.e(LOG_TAG,"Cursor failed to load.");
        }

        mForecastStr = String.format("%s - %s - %s/%s",date.getText(),forecast.getText(),highT.getText(),lowT.getText());
        //date.setText(mForecastStr);
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //mForecastAdapter.swapCursor(null);
    }
}
