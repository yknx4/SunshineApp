package com.yknx.sunshineapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.yknx.sunshineapp.sync.SunshineSyncAdapter;


public class MainActivity extends ActionBarActivity implements ForecastFragment.CallbackListener {

    boolean mTwoPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if(findViewById(R.id.weather_detail_container)!=null){
            mTwoPanel = true;

            getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container,new ForecastDetailFragment()).commit();


        }else mTwoPanel=false;
        ForecastFragment mForeFrag = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        mForeFrag.setTwoPanel(mTwoPanel);
        SunshineSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String date) {
        if(mTwoPanel){
            Bundle args = new Bundle();
            args.putString(DetailActivity.DATE_KEY,date);

            ForecastDetailFragment fragment = new ForecastDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container,fragment).commit();
        }
        else {
            Intent inten = new Intent(this,DetailActivity.class).putExtra(DetailActivity.DATE_KEY,date);
            startActivity(inten);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */

}
