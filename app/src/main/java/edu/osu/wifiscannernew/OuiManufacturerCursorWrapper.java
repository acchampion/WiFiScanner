package edu.osu.wifiscannernew;

import android.database.Cursor;
import android.database.CursorWrapper;
import edu.osu.wifiscannernew.OuiManufacturerDbSchema.OuiManufacturerTable;

public class OuiManufacturerCursorWrapper extends CursorWrapper {

    OuiManufacturerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public OuiManufacturer getOuiManufacturer() {
        String oui = getString(getColumnIndex(OuiManufacturerTable.Cols.OUI));
        String manufacturer = getString(getColumnIndex(OuiManufacturerTable.Cols.MANUFACTURER));

        return new OuiManufacturer(oui, manufacturer);
    }
}
