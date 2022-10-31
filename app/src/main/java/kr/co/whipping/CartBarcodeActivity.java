package kr.co.whipping;

import androidx.appcompat.app.AppCompatActivity;

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

public class CartBarcodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_barcode);

        String barcodenums = (String) getIntent().getExtras().get("barcodenums");
        ImageView imageview = (ImageView) findViewById(R.id.iv_barcode_background);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(barcodenums, BarcodeFormat.EAN_13, 400, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageview.setImageBitmap(bitmap);
            imageview.invalidate();
            Log.d("dd", "바코드 이미지 생성 확인");

            imageview.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
            Log.d("dd", "바코드 이미지 생성 실패");
        }
    }
}