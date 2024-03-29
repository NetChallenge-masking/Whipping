package kr.co.whipping.scan.barcordscan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.co.whipping.Basket;
import kr.co.whipping.CartActivity;
import kr.co.whipping.CartBarcodeActivity;
import kr.co.whipping.DBHelper;
import kr.co.whipping.R;
import kr.co.whipping.scan.ScanActivity;
import kr.co.whipping.scan.camerascan.InnerCameraActivity;

public class BarcodeScanActivity extends AppCompatActivity {
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    DBHelper dbHelper;
    int count = 1;
    String barcodenums;
    byte[] byteArray;
    TextToSpeech tts;
    int clickCnt;

    Button minus ;
    Button plus ;
    Button cancel ;
    Button add ;
    Button back ;
    TextView prodCount ;
    TextView category ;
    TextView nameOfprod ;
    TextView price ;
    ImageView image_barcode_scan ;


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

    public void onBackPressed(){
        {
            Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        minus = (Button) findViewById(R.id.btn_count_down);
        plus = (Button) findViewById(R.id.btn_count_up);
        cancel = (Button) findViewById(R.id.btn_cancel);
        add = (Button) findViewById(R.id.btn_add);
        back = (Button) findViewById(R.id.btn_back_scan_barcode);
        prodCount = (TextView) findViewById(R.id.tv_item_count);
        category = (TextView) findViewById(R.id.tv_item_type_2);
        nameOfprod = (TextView) findViewById(R.id.tv_item_name_2);
        price = (TextView) findViewById(R.id.tv_item_price_2);
        image_barcode_scan = (ImageView) findViewById(R.id.image_barcode_scan);


//        dbHelper = new DBHelper(BarcodeScanActivity.this);
//        Cursor cursor = dbHelper.readAllBasket();
//        while (cursor.moveToNext()) {
//            barcodenums = cursor.getString(2);  //barcode_id
//            cursor.getString(3);  //barcode_type
//            nameOfprod.setText(cursor.getString(4));  //item_name
//            cursor.getInt(6);  //price
//        }


            if (resultCode == RESULT_OK) {
                Log.d("dd", "값 전달 확인");
                barcodenums = intent.getStringExtra("barcodenum");
                Log.d("dd", "값 setting 확인");
                Log.d("dd", barcodenums);

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                final int WIDTH = 400;
                final int HEIGHT = 300;

                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(barcodenums, BarcodeFormat.EAN_13, WIDTH, HEIGHT);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    /*Byte로 변환*/
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();

                    image_barcode_scan.setImageBitmap(bitmap);
                    image_barcode_scan.invalidate();
                    Log.d("dd", "바코드 이미지 생성 확인");

                    if (barcodenums.equals("8801051227208")) {
                        Log.d("dd", "샴푸 인식 확인");
                        category.setText("샴푸");
                        nameOfprod.setText("리엔 물들임 새치커버샴푸450ml(자연갈색)");
                        price.setText("15900");
                    }
                    else if (barcodenums.equals("8801046361252")){
                    category.setText("트리트먼트");
                    nameOfprod.setText("케라시스 데미지 클리닉 오리지널 컨디셔너 린스 1,000ml ");
                    price.setText("7900");
                    }
                    else if (barcodenums.equals("8801008700372")){
                        category.setText("가그린");
                        nameOfprod.setText("리스테린 그린티 마일드 750ml");
                        price.setText("6930");
                    }
                    else if (barcodenums.equals("8809685832560")){
                        category.setText("트리트먼트");
                        nameOfprod.setText("살롱10 트리트먼트 250ml (손상모발)");
                        price.setText("12900");
                    }
                    else if (barcodenums.equals("8809539444581")){
                        category.setText("트리트먼트");
                        nameOfprod.setText("미장센 트리트먼트 1000ml (스무디 실키)");
                        price.setText("7490");
                    }
                    else if (barcodenums.equals("4902430896689")){
                        Log.i("면도기", "면도기 인식");
                        category.setText("면도기");
                        nameOfprod.setText("질레트 마하5스포츠 면도날 8입");
                        price.setText("31900");
                    }
                    else if (barcodenums.equals("8801062633715")){
                        Log.i("초콜릿", "초콜릿 인식");
                        category.setText("간식");
                        nameOfprod.setText("드림카카오 72%");
                        price.setText("3500");
                    }

                } catch (WriterException e) {
                    e.printStackTrace();
                    Log.d("dd", "바코드 이미지 생성 실패");
                }



                //카테고리음성안내
                category.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemDetailTalkBack(category.getText().toString());
                    }
                });
                nameOfprod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemDetailTalkBack(nameOfprod.getText().toString());
                    }
                });
                price.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       itemDetailTalkBack(price.getText().toString()+"원");
                    }
                });

                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        count++;
                        itemDetailTalkBack("수량추가 현재수량"+count);
                        prodCount.setText((count + ""));
                    }
                });
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        count--;
                        itemDetailTalkBack("수량감소 현재수량"+count);
                        prodCount.setText((count + ""));
                    }
                });

                //취소
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        talkBack("담기취소");
                    }
                });

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        talkBack("뒤로가기");
                    }
                });

                //담기
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //음성안내 구현
                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
                            @Override
                            public void onInit(int i) {

                                if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                                    tts.setLanguage(Locale.KOREA);     //한국어로 설정
                                    tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                                    clickCnt++; //클릭 횟수
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (clickCnt == 1) { //한번 클릭했을 경우 버튼내용 음성안내
                                                tts.speak("상품 담기 버튼입니다."+nameOfprod.getText()+"을"+count+"개 담으시겠습니까? "+"활성화하려면 두번 탭하세요.", TextToSpeech.QUEUE_ADD, null);
                                            } else if (clickCnt == 2) { //두번 클릭했을 경우 다음 화면으로 intent

                                                DBHelper dbHelper = new DBHelper(BarcodeScanActivity.this);

                                                dbHelper.addBasket("1", barcodenums, "EAN-13", nameOfprod.getText().toString(),
                                                        prodCount.getText().toString(), price.getText().toString(), byteArray);

                                            }
                                            clickCnt = 0; //클릭횟수 0으로 초기화
                                        }

                                    }, 500); //클릭이 0.5초 이내로 한 번 더 클릭 되어있을 경우

                                }
                            }
                        });


                    }
                });
            }
        }

        //DB에서 상품 정보 가져오는 코드 추가
        //임시로 코드로 가져옴

//
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

//    public static String bitmapToByteArray(Bitmap bitmap) {
//        String image = "";
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        image = byteArrayToBinaryString(byteArray);
//        return image;
//    }
//
//    public static String byteArrayToBinaryString(byte[] b) {
//        StringBuilder sb = new StringBuilder();
//        for(int i = 0; i < b.length; i++) {
//            sb.append(byteToBinaryString(b[i]));
//        }
//        return sb.toString();
//    }
//
//    public static String byteToBinaryString(byte n) {
//        StringBuilder sb = new StringBuilder();
//        for(int bit = 0; bit < 8; bit++) {
//            if(((n >> bit) & 1) > 0) {
//                sb.setCharAt(7 - bit, '1');
//            }
//        }
//        return sb.toString();
//    }
    //인텐트 넘겨주는 버튼을 위한 접근성 음성안내 함수
    public void talkBack(String text,Class intentClassName){
        //음성안내 구현
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
            @Override
            public void onInit(int i) {

                if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                    tts.setLanguage(Locale.KOREA);     //한국어로 설정
                    tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                    clickCnt++; //클릭 횟수
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (clickCnt == 1) { //한번 클릭했을 경우 버튼내용 음성안내
                                tts.speak(text +" 버튼입니다. 활성화하려면 두번 탭하세요.", TextToSpeech.QUEUE_ADD, null);
                            } else if (clickCnt == 2) { //두번 클릭했을 경우 다음 화면으로 intent

                                Intent intent = new Intent(getApplicationContext(), intentClassName);
                                startActivity(intent);

                            }
                            clickCnt = 0; //클릭횟수 0으로 초기화
                        }

                    }, 500); //클릭이 0.5초 이내로 한 번 더 클릭 되어있을 경우

                }
            }
        });
    }
    public void itemDetailTalkBack(String text){
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
            @Override
            public void onInit(int i) {

                if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                    tts.setLanguage(Locale.KOREA);     //한국어로 설정
                    tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                    tts.speak(text, TextToSpeech.QUEUE_ADD, null);
                }
            }
        });
    }
    //인텐트 넘겨주는 않는 뒤로가기 또는 종료 버튼을 위한 접근성 음성안내 함수
    public void talkBack(String text){
        //음성안내 구현
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
            @Override
            public void onInit(int i) {

                if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                    tts.setLanguage(Locale.KOREA);     //한국어로 설정
                    tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                    clickCnt++; //클릭 횟수
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (clickCnt == 1) { //한번 클릭했을 경우 버튼내용 음성안내
                                tts.speak(text +" 버튼입니다. 활성화하려면 두번 탭하세요.", TextToSpeech.QUEUE_ADD, null);
                            } else if (clickCnt == 2) { //두번 클릭했을 경우 다음 화면으로 intent
                                finish();
                            }
                            clickCnt = 0; //클릭횟수 0으로 초기화
                        }

                    }, 500); //클릭이 0.5초 이내로 한 번 더 클릭 되어있을 경우

                }
            }
        });
    }

}