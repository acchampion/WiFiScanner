package edu.osu.wifiscannernew;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.osu.wifiscannernew.OuiManufacturerDbSchema.OuiManufacturerTable;

public class OuiManufacturerDbHelper extends SQLiteOpenHelper {
    private Context mContext;
    private static final String DATABASE_NAME = "oui.db";
    private static final int DATABASE_VERSION = 1;

    // Class name for logging.
    private final String TAG = getClass().getSimpleName();

    public OuiManufacturerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + OuiManufacturerTable.NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                OuiManufacturerTable.Cols.OUI + " TEXT, " +
                OuiManufacturerTable.Cols.MANUFACTURER + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database; dropping and recreating tables");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OuiManufacturerTable.NAME);
        onCreate(sqLiteDatabase);
    }
}
