package edu.osu.wifiscannernew;

import android.support.v4.app.Fragment;

public class WifiScanActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new WifiScanFragment();
    }
}
