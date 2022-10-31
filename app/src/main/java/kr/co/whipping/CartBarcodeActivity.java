package kr.co.whipping;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CartBarcodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_barcode);

        ImageView imageview = findViewById(R.id.iv_barcode_background);

        DBHelper dbHelper = new DBHelper(CartBarcodeActivity.this);
        Cursor cursor = dbHelper.readBarcodeImg();

        ArrayList<byte[]> imgList = new ArrayList<>();
        while (cursor.moveToNext()) {
            byte[] img = cursor.getBlob(0);

            imgList.add(img);
        }

        for(byte[] b : imgList) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            imageview.setImageBitmap(bitmap);
        }
    }
}