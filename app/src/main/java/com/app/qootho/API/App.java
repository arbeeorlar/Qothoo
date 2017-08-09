package com.app.qootho.API;

import android.app.Application;

import co.paystack.android.PaystackSdk;

/**
 * Created by macbookpro on 29/07/2017.
 */

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        PaystackSdk.initialize(getApplicationContext());

    }
}
