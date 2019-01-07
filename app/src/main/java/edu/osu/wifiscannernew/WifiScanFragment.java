package edu.osu.wifiscannernew;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for Wi-Fi scans.
 *
 * Created by adamcchampion on 2018/01/05.
 */

@SuppressLint("LogNotTimber")
public class WifiScanFragment extends Fragment {
    /*
     * ************************************************************************
     * Declare class scoped fields. Notice they are prefixed with "m" (for most
     * variables) or "s" (for services).
     * ************************************************************************
     */
    private WifiManager mWifiManager;
    private IntentFilter mIntentFilter;

    private ScanResultAdapter mScanResultAdapter;

    private List<ScanResult> mScanResultList = new ArrayList<>();

    private static final int PERMISSION_REQUEST_LOCATION = 1;
    private final String TAG = getClass().getSimpleName();

    /*
     * ************************************************************************
     * Declare a Broadcast Receiver that "responds" to Android system Intents.
     * In our case, we only want to display the results of a WiFi scan, which
     * are made available when the SCAN_RESULTS_AVAILABLE_ACTION fires (after
     * the scan is completed).
     * ************************************************************************
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        // Override onReceive() method to implement our custom logic.
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // Get the Intent action.
            String action = intent.getAction();

            // If the WiFi scan results are ready, iterate through them and
            // record the WiFi APs' SSIDs, BSSIDs, WiFi capabilities, radio
            // frequency, and signal strength (in dBm).
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action))
            {
                // Ensure WifiManager is not null first.
                if (mWifiManager == null) {
                    setupWifi();
                }

                mScanResultList.clear();
                List<ScanResult> scanResults = mWifiManager.getScanResults();
                Log.d(TAG, "Wi-Fi scan results available");
                mScanResultList.addAll(scanResults);
                mScanResultAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * Inflate the Fragment view for Wi-Fi scans.
     *
     * @param inflater LayoutInflater that inflates XML view
     * @param container The parent view container
     * @param savedInstanceState Any previous saved state
     * @return Created Fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wifi_scan, container, false);

        RecyclerView scanResultRecyclerView = v.findViewById(R.id.scan_result_recyclerview);
        mScanResultAdapter = new ScanResultAdapter(mScanResultList);
        scanResultRecyclerView.setAdapter(mScanResultAdapter);
        scanResultRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupWifi();
        mIntentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        Context context = getActivity();
        if (context != null) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            boolean hideDialog = sharedPreferences.getBoolean(
                    getResources().getString(R.string.suppress_dialog_key), false);
            if (!hideDialog) {
                Log.d(TAG, "Showing permission info dialog to user");
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment fragment = new NoticeDialogFragment();
                fragment.show(fm, "info_dialog");
            }

            try {
                getActivity().registerReceiver(mReceiver, mIntentFilter);
            } catch (NullPointerException npe) {
                Log.d(TAG, "Error registering BroadcastReceiver");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            Context context = getActivity();
            if (context != null) {
                context.unregisterReceiver(mReceiver);
            }
        } catch (NullPointerException npe) {
            Log.d(TAG, "Error un-registering BroadcastReceiver");
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                Log.d(TAG, "Request Wi-Fi scan");
                if (hasNoLocationPermission()) {
                    requestLocationPermission();
                }
                else {
                    doWifiScan();
                }
                return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doWifiScan();
            }
            else {
                Log.e(TAG, "Error: Permission denied to read location");
                Toast.makeText(getActivity(), getResources().getString(R.string.read_location_permission_denied),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupWifi() {
        try {
            Context context = getActivity();
            if (context != null) {
                mWifiManager = (WifiManager) context.getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);
            }
        } catch (NullPointerException npe) {
            Log.e(TAG, "Error setting up Wi-Fi");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasNoLocationPermission() {
        Activity activity = getActivity();
        return activity == null ||
                activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED;
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasNoLocationPermission()) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_LOCATION);
            }
        }
    }

    private void doWifiScan() {
        if (mWifiManager == null) {
            setupWifi();
        }

        boolean scanRetVal = mWifiManager.startScan();
        if (!scanRetVal) {
            Log.e(TAG, "Error scanning for Wi-Fi");
        }
    }

    private class ScanResultHolder extends RecyclerView.ViewHolder {

        private ProgressBar mSignalStrengthBar;
        private TextView mScanResultSsid;
        private TextView mScanResultRssi;
        private TextView mScanResultChannel;
        private ScanResult mScanResult;

        ScanResultHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_wifi_scan, parent, false));

            mScanResultSsid = itemView.findViewById(R.id.scan_result_textview);
            mScanResultRssi = itemView.findViewById(R.id.scan_result_rssi);
            mScanResultChannel = itemView.findViewById(R.id.scan_result_channel);
            mSignalStrengthBar = itemView.findViewById(R.id.scan_result_rssi_bar);
        }

        void bind(ScanResult scanResult) {
            mScanResult = scanResult;

            int signalLevel = WifiManager.calculateSignalLevel(mScanResult.level,
                    Constants.NUM_PROGRESS_LEVELS);
            mSignalStrengthBar.setProgress(signalLevel);

            String rssiStr = mScanResult.level + " dBm";
            mScanResultRssi.setText(rssiStr);

            String resultTextStr = mScanResult.SSID + " (" + mScanResult.BSSID + ")";
            mScanResultSsid.setText(resultTextStr);

            Integer channel = -1;
            float freq = mScanResult.frequency / Constants.MHZ_PER_GHZ;

            String channelStr, channelWidthStr = "";
            if (Constants.FREQUENCY_CHANNEL_MAP.containsKey(mScanResult.frequency)) {
                channel = Constants.FREQUENCY_CHANNEL_MAP.get(mScanResult.frequency);
            }

            if (mScanResult.channelWidth == ScanResult.CHANNEL_WIDTH_20MHZ) {
                channelWidthStr = "20 MHz";
            }
            else if (mScanResult.channelWidth == ScanResult.CHANNEL_WIDTH_40MHZ) {
                channelWidthStr = "40 MHz";
            }
            else if (mScanResult.channelWidth == ScanResult.CHANNEL_WIDTH_80MHZ) {
                channelWidthStr = "80 MHz";
            }
            else if (mScanResult.channelWidth == ScanResult.CHANNEL_WIDTH_80MHZ_PLUS_MHZ) {
                channelWidthStr = "80 + 80 MHz";
            }
            else if (mScanResult.channelWidth == ScanResult.CHANNEL_WIDTH_160MHZ) {
                channelWidthStr = "160 MHz";
            }

            if (channel > 0) {
                channelStr = "Ch. " + channel + " (" + freq + " GHz)";
            }
            else {
                channelStr = freq + " GHz";
            }

            if (channelWidthStr.length() > 0) {
                channelStr = channelStr + "; width: " + channelWidthStr;
            }

            mScanResultChannel.setText(channelStr);
        }
    }

    private class ScanResultAdapter extends RecyclerView.Adapter<ScanResultHolder> {

        private List<ScanResult> mScanResultList;

        ScanResultAdapter(List<ScanResult> scanResultList) {
            mScanResultList = scanResultList;
        }

        @NonNull
        @Override
        public ScanResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new ScanResultHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ScanResultHolder holder, int position) {
            ScanResult scanResult = mScanResultList.get(position);
            holder.bind(scanResult);
        }

        @Override
        public int getItemCount() {
            if (mScanResultList != null) {
                return mScanResultList.size();
            }
            else return 0;
        }
    }
}
