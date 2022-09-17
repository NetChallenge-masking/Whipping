package kr.co.whipping;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper
{
    private Context context;
    private static final String DATABASE_NAME = "Whipping.db";
    private static final int DATABASE_VERSION = 1;


    public DBHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String basketQuery = "CREATE TABLE " + "basket"
                + " (" + "basket_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "device" + " INTEGER, "
                + "barcode_id" + " VARCAHR(30), "
                + "barcode_type" + " VARCAHR(30), "
                + "amount" + " INTEGER, "
                + "CONSTRAINT barcode_id FOREIGN KEY (barcode_id) REFERENCES item(barcode_id), "
                + "CONSTRAINT barcode_type FOREIGN KEY (barcode_type) REFERENCES item(barcode_type)); ";
        db.execSQL(basketQuery);

        String itemQuery = "CREATE TABLE " + "item"
                + " (" + "barcode_id" + " VARCAHR(30) PRIMARY KEY, "
                + "barcode_type" + " VARCAHR(30), "
                + "item_name" + " VARCAHR(30), "
                + "price" + " INTEGER, "
                + "category" + " VARCAHR(30), "
                + "beacon_id" + " INTEGER,"
                + "CONSTRAINT beacon_id FOREIGN KEY (beacon_id) REFERENCES beacon(beacon_id)); ";
        db.execSQL(itemQuery);

        String beaconQuery = "CREATE TABLE " + "beacon"
                + " (" + "beacon_id" + " INTEGER PRIMARY KEY, "
                + "location" + " VARCAHR(30), "
                + "event" + " TEXT, "
                + "facility" + " VARCAHR(30), "
                + "stand_info" + " VARCAHR(30), "
                + "update_date" + " TIMESTAMP); ";
        db.execSQL(beaconQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + "basket");
        db.execSQL("DROP TABLE IF EXISTS " + "item");

        onCreate(db);
    }

    public void addBasket(String device, String barcodeId, String barcodeType, String amount)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("device", device);
        cv.put("barcode_id", barcodeId);
        cv.put("barcode_type", barcodeType);
        cv.put("amount", amount);
        long result = db.insert("basket", null, cv);
        if (result == -1)
        {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "데이터 추가 성공", Toast.LENGTH_SHORT).show();
        }
    }

    void addItem(String barcodeId, String barcodeType, String itemName, String price, String category)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("barcode_id", barcodeId);
        cv.put("barcode_type", barcodeType);
        cv.put("item_name", itemName);
        cv.put("price", price);
        cv.put("category", category);
        long result = db.insert("item", null, cv);
        if (result == -1)
        {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "데이터 추가 성공", Toast.LENGTH_SHORT).show();
        }
    }

}