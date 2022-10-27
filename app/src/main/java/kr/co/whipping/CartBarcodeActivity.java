package kr.co.whipping;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class CartBarcodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_barcode);

        ImageView imageview = (ImageView)findViewById(R.id.iv_barcode_background);

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = getIntent().getByteArrayExtra("barcodeImg");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageview.setImageBitmap(bitmap);

    }
}