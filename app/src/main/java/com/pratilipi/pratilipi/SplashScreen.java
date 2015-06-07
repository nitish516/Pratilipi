package com.pratilipi.pratilipi;

/**
 * Created by Nitish on 29-04-2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}