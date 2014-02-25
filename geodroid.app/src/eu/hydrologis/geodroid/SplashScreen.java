package eu.hydrologis.geodroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * @author root
 *
 */
public class SplashScreen extends Activity {
    // Splash screen timer

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable(){

            public void run() {

                // This method will be executed once the timer is over
                // Start your app main activity

                Intent i = new Intent(SplashScreen.this, GeoDroidActivity.class);

                startActivity(i);

                // close this activity

                finish();

            }

        }, SPLASH_TIME_OUT);

    }

}
