package kr.co.whipping.scan.camerascan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    //private ImageView mMainImage;
    private long startTimeMS;
    private float uploadDurationSec;
//    private Button speak_out;
//    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerascan);

        //음성 안내 삭제
//        tts = new TextToSpeech(this,this);
        mImageDetails = findViewById(R.id.image_details);
        camerabackBtn = findViewById(R.id.cameraBackBtn);
        barcordGoBtn = findViewById(R.id.barcordScanGoBtn);

        Intent intent = getIntent(); //텍스트 인식 데이터 수신
        String textDetectionResult = intent.getExtras().getString("textDetectionResult");
        //바코드 스캔으로 바로가기 버튼
        mImageDetails.setText(textDetectionResult);

        barcordGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeScanActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //카메라 스캔으로 바로가기 버튼
        camerabackBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InnerCameraActivity.class);
                startActivity(intent);
                finish();
            }
        });


//        speak_out = findViewById(R.id.speak_out);
    }
//        speak_out.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                speakOut();
//            }
//        });

//    }
//    private void speakOut(){
//        CharSequence text = mImageDetails.getText().toString();
//        tts.setPitch((float)0.6); //음성 톤 높이 지정
//        tts.setSpeechRate((float)1.0); //음성 속도 지정
//        // 첫 번째 매개변수: 음성 출력을 할 텍스트
//        // 두 번째 매개변수: 1. TextToSpeech.QUEUE_FLUSH - 진행중인 음성 출력을 끊고 이번 TTS의 음성 출력
//        //                 2. TextToSpeech.QUEUE_ADD - 진행중인 음성 출력이 끝난 후에 이번 TTS의 음성 출력
//        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
//        Toast.makeText(this, mImageDetails.getText().toString(), Toast.LENGTH_LONG).show();
//
//    }
//    @Override
//    public void onDestroy() {
//        if(tts!=null){ // 사용한 TTS객체 제거
//            tts.stop();
//            tts.shutdown();
//        }
//        super.onDestroy();
//    }
//    @Override
//    public void onInit(int status) { //OnInitListener를 통해 TTS초기화
//        if(status == TextToSpeech.SUCCESS){
//            int result = tts.setLanguage(Locale.US); // TTS언어 한국어로 설정
//
//            if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
//                Log.e("TTS", "This Language is not supported");
//            }else{
//                speak_out.setEnabled(true);
//                //speakOut();// onInit에 음성출력할 텍스트를 넣어줌
//            }
//        }else{
//            Log.e("TTS", "Initialization Failed!");
//        }
//    }



}
