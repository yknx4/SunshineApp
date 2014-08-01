package com.yknx.sunshineapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Yknx on 31/07/2014.
 */
public class ForecastDetailFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent originalMessage = getActivity().getIntent();
        TextView data = (TextView) rootView.findViewById(R.id.textview_forecastData);
        mForecastStr = originalMessage.getStringExtra(Intent.EXTRA_TEXT);
        data.setText(mForecastStr);

        return rootView;
    }
}
