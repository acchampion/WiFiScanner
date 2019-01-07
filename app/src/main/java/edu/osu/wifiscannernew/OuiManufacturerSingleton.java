package edu.osu.wifiscannernew;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import edu.osu.wifiscannernew.OuiManufacturerDbSchema.OuiManufacturerTable;

public class OuiManufacturerSingleton {
    private static OuiManufacturerSingleton sOuiManufacturer;

    private SQLiteDatabase mDatabase;

    private static final String INSERT_STMT = "INSERT INTO " + OuiManufacturerTable.NAME +
            " (oui, manufacturer) VALUES (?, ?)" ;


    public static OuiManufacturerSingleton get(Context context) {
        if (sOuiManufacturer == null) {
            sOuiManufacturer = new OuiManufacturerSingleton(context);
        }
        return sOuiManufacturer;
    }

    private OuiManufacturerSingleton(Context context) {
        OuiManufacturerDbHelper dbHelper = new OuiManufacturerDbHelper(context.getApplicationContext());
        mDatabase = dbHelper.getWritableDatabase();
    }

    private static ContentValues getContentValues(OuiManufacturer ouiManufacturer) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(OuiManufacturerTable.Cols.OUI, ouiManufacturer.getOui());
        contentValues.put(OuiManufacturerTable.Cols.MANUFACTURER, ouiManufacturer.getManufacturer());

        return contentValues;
    }

    /**
     * Add a new (OUI, manufacturer) pair to the database. This logic uses code from Jake Wharton:
     * http://jakewharton.com/kotlin-is-here/ (slide 61). It's much easier in Kotlin!
     *
     * @param ouiManufacturer OuiManufacturer object
     */
    public void addOuiManufacturer(OuiManufacturer ouiManufacturer) {
        ContentValues contentValues = getContentValues(ouiManufacturer);

        mDatabase.beginTransaction();
        try {
            SQLiteStatement statement = mDatabase.compileStatement(INSERT_STMT);
            statement.bindString(1, contentValues.getAsString(OuiManufacturerTable.Cols.OUI));
            statement.bindString(2, contentValues.getAsString(OuiManufacturerTable.Cols.MANUFACTURER));
            statement.executeInsert();
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }

    /**
     * Delete all (OUI, manufacturer) pairs from the database. This logic uses code from Jake Wharton:
     * http://jakewharton.com/kotlin-is-here/ (slide 61). It's much easier in Kotlin!
     */
    public void deleteAllOuiManufacturers() {
        mDatabase.beginTransaction();
        try {
            mDatabase.delete(OuiManufacturerTable.NAME, null, null);
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }

    private OuiManufacturerCursorWrapper queryOuisManufacturers(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                OuiManufacturerTable.NAME,
                null, // columns; null selects all columns
                whereClause,
                whereArgs,
                null, // GROUP BY
                null, // HAVING
                null // ORDER BY
        );

        return new OuiManufacturerCursorWrapper(cursor);
    }

    public List<OuiManufacturer> getOuisManufacturers(String oui) {
        List<OuiManufacturer> ouiManufacturerList = new ArrayList<>();
        String[] ouiArr = new String[] {oui};
        OuiManufacturerCursorWrapper cursor = queryOuisManufacturers("oui = '?'", ouiArr);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ouiManufacturerList.add(cursor.getOuiManufacturer());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return ouiManufacturerList;
    }
}
