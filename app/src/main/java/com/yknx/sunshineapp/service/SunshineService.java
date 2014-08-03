package com.yknx.sunshineapp.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yknx.sunshineapp.FetchWeatherTask;
import com.yknx.sunshineapp.data.WeatherContract;

/**
 * Created by Yknx on 03/08/2014.
 */
public class SunshineService extends IntentService {
    public static final String LOG_TAG = SunshineService.class.getSimpleName();
    public static final String EXTRA_LOCATIONSTRING = WeatherContract.CONTENT_AUTHORITY+".EXTRA_LOCATIONSTRING";
    public static final String ACTION_UPDATEWEATHER = WeatherContract.CONTENT_AUTHORITY+".action.UPDATEWEATHER";
    public SunshineService() {
        super("SunshineService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if(action==null)
        {
            Log.e(LOG_TAG,"No action called on SunshineService.");
            return;

        }
        Log.d(LOG_TAG,action+" called on SunshineService.");
        if(action.equals(ACTION_UPDATEWEATHER)){

            String dateToFetch = intent.getStringExtra(EXTRA_LOCATIONSTRING);
            FetchWeatherTask mTask = new FetchWeatherTask(getApplicationContext());

            mTask.execute(dateToFetch);

        }


    }

    public static class AlarmReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG,"Alarm intent received on SunshineService.");
            Intent updateWeather = new Intent(context, SunshineService.class);
            updateWeather.putExtra(SunshineService.EXTRA_LOCATIONSTRING,intent.getStringExtra(SunshineService.EXTRA_LOCATIONSTRING));
            updateWeather.setAction(intent.getAction());
            context.startService(updateWeather);
        }
    }
}
