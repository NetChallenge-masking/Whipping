package kr.co.whipping.scan.camerascan;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import kr.co.whipping.MainActivity;
import kr.co.whipping.R;
import kr.co.whipping.scan.barcordscan.BarcodeScanActivity;

public class CameraScanActivity extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDaNsgeV__6hcIrCllAdU1XAgj4OV29id4";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;
    private Button camerabackBtn;
    private Button barcordGoBtn;
    private Button readTextBtn;
    TextToSpeech tts;
    WhippingTTS whippingTTS;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerascan);
        readTextBtn=findViewById(R.id.btn_read_text);

        mImageDetails = findViewById(R.id.image_details);
        camerabackBtn = findViewById(R.id.cameraBackBtn);
        barcordGoBtn = findViewById(R.id.barcordScanGoBtn);

        Intent intent = getIntent(); //텍스트 인식 데이터 수신
        String textDetectionResult = intent.getExtras().getString("textDetectionResult");
        //바코드 스캔으로 바로가기 버튼
        mImageDetails.setText(textDetectionResult);

        tts= new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    whippingTTS=new WhippingTTS(textDetectionResult,tts);
                    whippingTTS.ttsResult();
                }
            }
        });

        readTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whippingTTS=new WhippingTTS(textDetectionResult,tts);
                whippingTTS.ttsResult();
            }
        });
        barcordGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeScanActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //카메라 스캔으로 바로가기 버튼
        camerabackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InnerCameraActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
        tts.shutdown();
    }
}


