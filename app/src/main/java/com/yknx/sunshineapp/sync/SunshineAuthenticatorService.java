package com.yknx.sunshineapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Yknx on 03/08/2014.
 */
public class SunshineAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private SunshineAuthenticator mAuthenticator;


    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new SunshineAuthenticator(this);
    }


    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
