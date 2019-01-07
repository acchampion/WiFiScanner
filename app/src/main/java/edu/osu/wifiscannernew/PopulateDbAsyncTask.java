package edu.osu.wifiscannernew;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PopulateDbAsyncTask extends AsyncTask<Context, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    @SuppressLint("LogNotTimber")
    @Override
    protected Void doInBackground(Context... params) {
        // Get the "oui_mfr.txt" file from assets, which contains lines in the format:
        //
        // OUI_n|manufacturer_n
        //
        // where the pipe character '|' is the delimiter and n indicates the line number. For each
        // line, create a corresponding OuiManufacturer object and insert it into the database.
        // (Asset handling code adapted from B. Phillips, C. Stewart, and K. Marsicano, Big Nerd
        // Ranch Guide to Android Programming, 3rd edition, Chapter 20.)
        if (params.length > 0) {
            Context context = params[0];

            AssetManager assetManager = context.getAssets();

            try {
                boolean assetsContainOuis = false;
                String[] assetPaths = assetManager.list(".");

                for (String assetPath: assetPaths) {
                    String[] components = assetPath.split("/");
                    String filename = components[components.length - 1];

                    if (filename.equals(Constants.OUI_FILENAME)) {
                        assetsContainOuis = true;
                        AssetFileDescriptor afd = assetManager.openFd(filename);
                        FileReader fileReader = new FileReader(afd.getFileDescriptor());
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        OuiManufacturerSingleton ouiManufacturerSingleton = OuiManufacturerSingleton.get(context);
                        ouiManufacturerSingleton.deleteAllOuiManufacturers();

                        while (bufferedReader.ready()) {
                            String line = bufferedReader.readLine();
                            String[] lineParts = line.split("\\|");
                            if (lineParts.length > 1) {
                                OuiManufacturer ouiManufacturer = new OuiManufacturer(lineParts[0], lineParts[1]);
                                ouiManufacturerSingleton.addOuiManufacturer(ouiManufacturer);
                            }
                            else {
                                Log.e(TAG, "Error parsing OUI file.");
                            }
                        }
                        bufferedReader.close();
                        fileReader.close();
                        afd.close();

                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(context.getResources().getString(R.string.loaded_db_key), true);
                        editor.apply();
                    }
                }
                if (!assetsContainOuis) {
                    Log.e(TAG, "Error: OUI file not found in assets");
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Could not load content from assets");
            }
        }

        return null;
    }
}
