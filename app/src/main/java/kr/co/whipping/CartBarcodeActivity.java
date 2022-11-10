package kr.co.whipping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

        DBHelper dbHelper = new DBHelper(CartBarcodeActivity.this);
        Cursor cursor = dbHelper.readBarcodeImg();
        Button btn_back_barcode = findViewById(R.id.btn_back_barcode);
        /*DB에 저장된 byte 가져오기*/
        ArrayList<byte[]> byteList = new ArrayList<>();
        while (cursor.moveToNext()) {
            byte[] img = cursor.getBlob(0);

            byteList.add(img);
        }

        /*byte를 bitmap으로 변환하기*/
        ArrayList<Bitmap> imgList = new ArrayList<>();
        for(byte[] b : byteList) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            imgList.add(bitmap);
        }

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        BarcodeImgAdapter adapter = new BarcodeImgAdapter(imgList);
        viewPager.setAdapter(adapter);

        btn_back_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}