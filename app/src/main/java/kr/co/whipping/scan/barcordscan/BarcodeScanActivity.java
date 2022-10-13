package kr.co.whipping.scan.barcordscan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.dynamsoft.dbr.BarcodeReader;
import com.dynamsoft.dbr.BarcodeReaderException;
import com.dynamsoft.dbr.DBRLicenseVerificationListener;
import com.dynamsoft.dbr.EnumImagePixelFormat;
import com.dynamsoft.dbr.TextResult;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.co.whipping.CartActivity;
import kr.co.whipping.DBHelper;
import kr.co.whipping.R;

public class BarcodeScanActivity extends AppCompatActivity {
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;


    int count = 1;
    String barcodenums;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);


        permission();

    }

    public void permission() {
        if (hasCameraPermission()) {
            Log.d("dd", "카메라실행전");
            startScan();
            return;

        } else {
            requestPermission();
        }
    }

    private void startScan() {
        Intent intent = new Intent(getApplicationContext(), BarcodeCameraActivity.class);
        startActivityForResult(intent, 1);
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScan();
                } else {
                    Toast.makeText(this, "Please grant camera permission", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Button minus = (Button) findViewById(R.id.btn_count_down);
        Button plus = (Button) findViewById(R.id.btn_count_up);
        Button cancel = (Button) findViewById(R.id.btn_cancel);
        Button add = (Button) findViewById(R.id.btn_add);
        Button back = (Button) findViewById(R.id.btn_back_scan_barcode);
        TextView prodCount = (TextView) findViewById(R.id.tv_item_count);
        TextView category = (TextView) findViewById(R.id.tv_item_type_2);
        TextView nameOfprod = (TextView) findViewById(R.id.tv_item_name_2);
        TextView price = (TextView) findViewById(R.id.tv_item_price_2);
        ImageView image_barcode_scan = (ImageView)findViewById(R.id.image_barcode_scan);
        if (resultCode == RESULT_OK) {
            Log.d("dd", "값 전달 확인");
            barcodenums = intent.getStringExtra("barcodenum");
            Log.d("dd", "값 setting 확인");
            Log.d("dd", barcodenums);
//            Toast.makeText(this, "RESULT_OK : " + barcodenums, Toast.LENGTH_SHORT).show();


            //          image_barcode_scan.setImageResource(R.drawable.btn_minus);


            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            final int WIDTH = 400;
            final int HEIGHT = 300;

            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(barcodenums, BarcodeFormat.EAN_13, WIDTH, HEIGHT);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                image_barcode_scan.setImageBitmap(bitmap);
                image_barcode_scan.invalidate();
                Log.d("dd", "바코드 이미지 생성 확인");
                //InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //manager.hideSoftInputFromWindow(barcodenums.getApplicationWindow, 0);
            } catch (WriterException e) {
                e.printStackTrace();
                Log.d("dd", "바코드 이미지 생성 실패");
            }


            if (barcodenums.equals("4902430232159")) {
                Log.d("dd", "샴푸 인식 확인");
                category.setText("샴푸");
                nameOfprod.setText("헤드&숄더 두피 토탈 솔루션 가려운 두피케어");
                price.setText("15,900");


//                MultiFormatWriter gen = new MultiFormatWriter();
//                String data = barcodenums;
//                try {
//                    final int WIDTH = 400;
//                    final int HEIGHT = 300;
//                    BitMatrix bytemap = gen.encode(data, BarcodeFormat.EAN_13, WIDTH, HEIGHT);
//                    Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
//                    for (int i = 0 ; i < WIDTH ; ++i)
//                        for (int j = 0 ; j < HEIGHT ; ++j) {
//                            bitmap.setPixel(i, j, bytemap.get(i,j) ? Color.BLACK : Color.WHITE);
//                        }
//                    ImageView img_barcode = (ImageView) findViewById(R.id.image_barcode);
//                    img_barcode.setImageBitmap(bitmap);
////                    img_barcode.invalidate();
//                    System.out.println("done!");}
//                catch (Exception e) {
//                    e.printStackTrace();}



            }


            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count++;
                    prodCount.setText((count + ""));
                }
            });
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count--;
                    prodCount.setText((count + ""));
                }
            });

            //취소
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            //담기
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DBHelper dbHelper = new DBHelper(BarcodeScanActivity.this);
//
//                dbHelper.addBasket("1", barcodenum, barcodetype, prodCount.getText().toString());
//

            //이미지
//                ImageView img_barcode = (ImageView) findViewById(R.id.image_barcode);

//                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
//                final int WIDTH = 180;
//                final int HEIGHT = 90;
//
//                try {
//                    BitMatrix bitMatrix = multiFormatWriter.encode(barcodenum, BarcodeFormat.valueOf(barcodetype), WIDTH, HEIGHT);
//                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
//                    img_barcode.setImageBitmap(bitmap);
//                } catch (Exception e) {
//                }


//
//            TextView etBarcode = (TextView) findViewById(R.id.etBarcode);
//            TextView etTyp = (TextView) findViewById(R.id.etTyp);
//            etBarcode.setText(barcodenum);
//            etTyp.setText(barcodetype);

        }


        //DB에서 상품 정보 가져오는 코드 추가
        //임시로 코드로 가져옴

//            else if (barcodenum.equals("8801046361252")){
//                category.setText("트리트먼트");
//                nameOfprod.setText("케라시스 데미지 클리닉 오리지널 컨디셔너 린스 1,000ml ");
//                price.setText("7,900");
//            }
//            else if (barcodenum.equals("8801008700372")){
//                category.setText("가그린");
//                nameOfprod.setText("리스테린 그린티 마일드 750ml");
//                price.setText("6,930");
//            }
//            else if (barcodenum.equals("8809685832560")){
//                category.setText("트리트먼트");
//                nameOfprod.setText("살롱10 트리트먼트 250ml (손상모발)");
//                price.setText("12,900");
//            }
//            else if (barcodenum.equals("8809539444581")){
//                category.setText("트리트먼트");
//                nameOfprod.setText("미장센 트리트먼트 1000ml (스무디 실키)");
//                price.setText("7,490");
//            }
//            else if (barcodenum.equals("4902430896689")){
//                Log.i("면도기", "면도기 인식");
////
//                category.setText("면도기");
//                nameOfprod.setText("질레트 마하5스포츠 면도날 8입");
//                price.setText("31,900");
//            }
//            else if (barcodenum.equals("8801062633715")){
//                Log.i("초콜릿", "초콜릿 인식");
////
//                category.setText("간식");
//                nameOfprod.setText("드림카카오 72%");
//                price.setText("3500");
//            }


//            TextView etBarcode = (TextView) findViewById(R.id.etBarcode);
//            TextView etTyp = (TextView) findViewById(R.id.etTyp);
//            etBarcode.setText(barcodenum);
//            etTyp.setText(barcodetype);

    }
    class ScanHandler {
        public void onScanned(String result) {
        }
    }

    public class JSInterface {
        private ScanHandler mHandler;

        JSInterface(ScanHandler handler) {
            mHandler = handler;
        }

        @JavascriptInterface
        public void returnResult(String result) {
            mHandler.onScanned(result);
        }
    }
}