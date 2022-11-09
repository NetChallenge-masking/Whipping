package kr.co.whipping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

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

    //테이블 생성
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String basketQuery = "CREATE TABLE " + "basket"
                + " (" + "basket_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "device" + " INTEGER, "
                + "barcode_id" + " VARCAHR(30), "
                + "barcode_type" + " VARCAHR(30), "
                + "item_name" + " VARCAHR(30), "
                + "amount" + " INTEGER, "
                + "price" + " INTEGER, "
                + "barcode_img" + " BLOB, "
                + "CONSTRAINT barcode_id FOREIGN KEY (barcode_id) REFERENCES item(barcode_id), "
                + "CONSTRAINT barcode_type FOREIGN KEY (barcode_type) REFERENCES item(barcode_type)); ";
        db.execSQL(basketQuery);

        String itemQuery = "CREATE TABLE " + "item"
                + " (" + "barcode_id" + " VARCAHR(30) PRIMARY KEY, "
                + "item_name" + " VARCAHR(30), "
                + "item_location" + " VARCAHR(30));";
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

    //장바구니 추가
    public void addBasket(String device, String barcodeId, String barcodeType, String itemName, String amount, String price, byte[] barcode_img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("device", device);
        cv.put("barcode_id", barcodeId);
        cv.put("barcode_type", barcodeType);
        cv.put("item_name", itemName);
        cv.put("amount", amount);
        cv.put("price", price);
        cv.put("barcode_img", barcode_img);
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

    //장바구니 읽기
    public Cursor readAllBasket() {
        String query = "SELECT * FROM basket";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    //장바구니 삭제
    public void deleteBasket(int basketId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM basket WHERE basket_id = '" + basketId + "';");
    }

    //수량 추가
    public void addAmount(int basketId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE basket SET amount = amount + 1 WHERE basket_id = '" + basketId + "';");
    }

    //수량 삭제
    public void minusAmount(int basketId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE basket SET amount = amount - 1 WHERE basket_id = '" + basketId + "';");
    }

    //바코드 이미지 읽기
    public Cursor readBarcodeImg() {
        String query = "SELECT barcode_img FROM basket";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    //상품 넣기
    public void addItem(String barcodeId, String itemName, String itemLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("barcode_id", barcodeId);
        cv.put("item_name", itemName);
        cv.put("item_location", itemLocation);
        long result = db.insert("item", null, cv);
    }

    //상품 위치 가져오기
    public Cursor readItemLocation(String searchItem) {
        String query = "SELECT item_name, item_location FROM item WHERE item_name LIKE '%" + searchItem + "%';";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}