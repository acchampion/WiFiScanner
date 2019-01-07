package edu.osu.wifiscannernew;

public class OuiManufacturer {
    private String mOui;
    private String mManufacturer;

    public OuiManufacturer(String oui, String manufacturer) {
        mOui = oui;
        mManufacturer = manufacturer;
    }

    public String getOui() {
        return mOui;
    }

    public String getManufacturer() {
        return mManufacturer;
    }
}
