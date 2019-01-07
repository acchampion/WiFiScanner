package edu.osu.wifiscannernew;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WifiScannerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean hasLoadedDb = sharedPreferences.getBoolean(getString(R.string.loaded_db_key), false);
        if (!hasLoadedDb) {
            // TODO: Call PopulateDbAsync to populate database.
        }
    }
}
