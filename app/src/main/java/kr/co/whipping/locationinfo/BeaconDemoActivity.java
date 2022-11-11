package kr.co.whipping.locationinfo;

import static com.minew.beacon.BluetoothState.BluetoothStatePowerOff;
import static com.minew.beacon.BluetoothState.BluetoothStatePowerOn;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import java.util.List;
import java.util.Locale;

import kr.co.whipping.R;
import kr.co.whipping.scan.barcordscan.BarcodeScanActivity;
import kr.co.whipping.scan.camerascan.InnerCameraActivity;

public class BeaconDemoActivity extends AppCompatActivity {
    TextToSpeech tts;
    int clickCnt = 0;
    private TextView beaconInfo1TextView;
    private TextView beaconInfo2TextView;
    private Button itemInfoBtn;
    private Button barcodeInfoBtn;
    private ImageView backBtn;
    private ImageView leftBtn;
    private ImageView rightBtn;
    private MinewBeaconManager mMinewBeaconManager;
    private boolean isScanning;
    private static final int REQUEST_ALL_PERMISSIONS = 2;
    //위치권한
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    //위치권한+블루투스 권한
    private static final String[] PERMISSIONS_S_ABOVE = {
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_demoactivity);
        initView();
        initManager();
        //모든 권한 확인하기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermissions(this, PERMISSIONS_S_ABOVE)) {
                requestPermissions(PERMISSIONS_S_ABOVE, REQUEST_ALL_PERMISSIONS);
            }
            //권한이 없다면 권한 요구하기
        } else {
            if (!hasPermissions(this, PERMISSIONS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSIONS);
                }
            }
        }
        initListener();

    }

    //비콘 탐색 시작 버튼
    private void initListener() {

        if (mMinewBeaconManager != null) {
            BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
            switch (bluetoothState) { //블루투스 권한
                case BluetoothStateNotSupported:
                    Toast.makeText(BeaconDemoActivity.this, "Not Support BLE", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case BluetoothStatePowerOff:
                    return;
                case BluetoothStatePowerOn:
                    break;
            }
        }
        if (isScanning) {
            isScanning = false;
            if (mMinewBeaconManager != null) {
                mMinewBeaconManager.stopScan();
            }
        } else {
            isScanning = true;

            try {
                mMinewBeaconManager.startScan(); //스캔시작
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mMinewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {

            /**
             * if the manager find some new beacon, it will call back this method.
             *
             * @param minewBeacons new beacons the manager scanned
             */
            @Override
            public void onAppearBeacons(List<MinewBeacon> minewBeacons) {
                //Log.e("onAppearBeacons()","실행");


            }

            /**
             * if a beacon didn't update data in 10 seconds, we think this beacon is out of rang, the manager will call back this method.
             *
             * @param minewBeacons beacons out of range
             */
            @Override
            public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {


            }

            /**
             * the manager calls back this method every 1 seconds, you can get all scanned beacons.
             *
             * @param minewBeacons all scanned beacons
             */
            @Override
            public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
                if (!minewBeacons.isEmpty()) { //근처에 비콘이 없을 경우가 아니라면
                    String beaconName = minewBeacons.get(0).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    Log.e("가장가까운 비콘 이름", beaconName);
                    //편의시설 정보 위치
                    if (beaconName.equals("beacon9")) {
                        setBeaconFacilitiesInfo("2층 엘레베이터");
                    } else if (beaconName.equals("beaconA")) {
                        setBeaconFacilitiesInfo("2층 화장실");
                    }

                    //시나리오 작성
                    //1. 매장입구
                    else if (beaconName.equals("beaconB")) {
                        Log.e("beaconB인식", "화면 문구 : 1층 매장입구, 음성안내문구 : 1층 매장입구 입니다.");
                        setBeaconFacilitiesInfo("2층 매장입구");
                    }
                    //2. 1층 계산대
                    else if (beaconName.equals("beaconC")) {
                        setBeaconFacilitiesInfo("2층 계산대");
                    }
                    //3. 생활용품 / 스포츠매대
                    else if (beaconName.equals("beacon8")) {
                        //생활용품, 스포츠 매대를 인색했을 경우
                        //매장입구, 계산대 방향으로 오는 경우
                        setBeaconItemInfo("생활용품", "스포츠");
                        //setBeaconItemInfo("스포츠", "생활용품");
                    }

                    //4.헤어용품/ 구강용품 매대
                    if (beaconName.equals("beacon1")) {
                        //생활용품,스포츠 매대 방향으로 오는 경우
                        setBeaconItemInfo("헤어용품", "구강용품");

                        //setBeaconItemInfo("구강용품", "헤어용품");

                    }
                    //5. 샴푸/면도기 매대
                    else if (beaconName.equals("beacon2") || beaconName.equals("beacon3")) {
                        //헤어용품, 구강용품을 먼저 인식 했을 경우
                        setBeaconItemInfo("샴푸", "면도기");
                        // 구강용품, 헤어용품을 먼저 인식 했을 경우
                        //setBeaconItemInfo("면도기", "샴푸");

                    }
                    //6. 트린트먼트/의약제품 및 가그린 매대
                    else if (beaconName.equals("beacon4") || beaconName.equals("beacon5")) {
                        //헤어용품/구강용품을 먼저 인식했거나 샴푸/면도기를 먼저 인식했을 경우
                        setBeaconItemInfo("트리트먼트", "의약제품 및 가그린 ");
                        //setBeaconItemInfo("의약제품 및 가그린", "트리트먼트");

                    }
                    //7. 트리트먼트/면도기
                    else if (beaconName.equals("beacon6") || beaconName.equals("beacon7")) {
                        //헤어용품, 구강용품을 먼저 인식했거나 트리트먼트/의약제품 및 가그린 먼저 인식했을 경우
                        setBeaconItemInfo("트리트먼트", "면도기");
                        //setBeaconItemInfo("면도기", "트리트먼트");

                    }
                    //8.  행사상품 안내
                    else if (beaconName.equals("beaconG")) {
                        setBeaconSaleInfo("2+1행사상품 \n 리엔 물들임 새치커버샴푸450ml(자연갈색)", "2+1행사상품 \n 리엔 물들임 트리트먼트 300ml(흑갈색)");
                    }
                    //9. 구강용품/헤어용품
                    else if (beaconName.equals("beaconF")) {
                        setBeaconItemInfo("구강용품", "헤어용품");
                    } else if (beaconName.equals("beaconE")) {
                        setBeaconFacilitiesInfo("2층으로 가는 에스컬레이터");
                    } else if (beaconName.equals("beaconD")) {
                        setBeaconItemInfo("생활용품", "가전");
                        //setBeaconItemInfo("가전", "생활용품");
                    }
                }
            }


            /**
             * the manager calls back this method when BluetoothStateChanged.
             *
             * @param state BluetoothState
             */
            //블루투스 켜졌을 경우, 꺼졌을 경우
            @Override
            public void onUpdateState(BluetoothState state) {
                switch (state) {
                    case BluetoothStatePowerOn:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOn", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothStatePowerOff:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOff", Toast.LENGTH_SHORT).show();
                        break;
                }
            }




        });

    }


    private boolean hasPermissions(Context context, String[] permissions){
        for(String permission : permissions){
            if(ActivityCompat.checkSelfPermission(context,permission)
                    != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    //permission check
    @RequiresApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ALL_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissions(permissions, REQUEST_ALL_PERMISSIONS);
                }
        }


    }

    private void setTextItem(int clickCnt){
            switch (clickCnt){
                case 1 :
                    beaconInfo1TextView.setText("2층으로 가는 엘레베이터");
                    beaconInfo2TextView.setText("2층으로 가는 엘레베이터");
                    break;
                case 2:
                    beaconInfo1TextView.setText("2+1행사상품 \n 리엔 물들임 새치커버샴푸450ml(자연갈색)");
                    beaconInfo2TextView.setText("2+1행사상품 \n 리엔 물들임 트리트먼트 300ml(흑갈색)");
                    break;
                case 3:
                    beaconInfo1TextView.setText("2층 매장 입구");
                    beaconInfo2TextView.setText("2층 매장 입구");
                    break;
                case 4:
                    beaconInfo1TextView.setText("잡화");
                    beaconInfo2TextView.setText("2층 계산대");
                    break;
                case 5:
                case 10:
                    beaconInfo1TextView.setText("헤어용품 및 구강용품");
                    beaconInfo2TextView.setText("헤어용품 및 구강용품");
                    break;
                case 6:
                    beaconInfo1TextView.setText("샴푸");
                    beaconInfo2TextView.setText("면도기");
                    break;
                case 7:
                    beaconInfo1TextView.setText("트리트먼트");
                    beaconInfo2TextView.setText("면도기");
                    break;
                case 8:
                    beaconInfo1TextView.setText("트리트먼트");
                    beaconInfo2TextView.setText("의약제품 및 가그린");
                    break;
                case 9:
                    beaconInfo1TextView.setText("2+1행사상품 \n 리엔 물들임 새치커버샴푸450ml(자연갈색)");
                    beaconInfo2TextView.setText("2+1행사상품 \n 리엔 물들임 트리트먼트 300ml(흑갈색)");
                    break;
                case 11:
                    beaconInfo1TextView.setText("의약제품 및 가그린");
                    beaconInfo2TextView.setText("트리트먼트");
                    break;
                case 12:
                    beaconInfo1TextView.setText("면도기");
                    beaconInfo2TextView.setText("트리트먼트");
                    break;
                case 13:
                    beaconInfo1TextView.setText("면도기");
                    beaconInfo2TextView.setText("샴푸");
                    break;
                case 14:
                    beaconInfo1TextView.setText("헤어용품 및 구강용품");
                    beaconInfo2TextView.setText("헤어용품 및 구강용품");
                    clickCnt=0;
                    break;
                default:
                    clickCnt=0;
                    beaconInfo1TextView.setText("매대이름1");
                    beaconInfo2TextView.setText("매대이름2");
            }

    }

    //비콘 매니저 실행
    private void initManager() {
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
    }
    private void initView() {
        clickCnt=0;
        backBtn=(ImageView)findViewById(R.id.backBtn);
        beaconInfo1TextView = (TextView) findViewById(R.id.beaconInfo1TextView);
        beaconInfo2TextView = (TextView) findViewById(R.id.beaconInfo2TextView);
        itemInfoBtn = (Button) findViewById(R.id.itemInfoTextView);
        barcodeInfoBtn = (Button) findViewById(R.id.barcodeInfoBtn);
        leftBtn=(ImageView) findViewById(R.id.leftItemBtn);
        rightBtn=(ImageView) findViewById(R.id.rightItemBtn);






        itemInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("상품정보 확인하기", InnerCameraActivity.class);
            }
        });
        barcodeInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talkBack("바코드로 상품담기", BarcodeScanActivity.class);
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCnt--;
                if(clickCnt<0 ||clickCnt>12) clickCnt=0;
                setTextItem(clickCnt);
                Log.d("클릭수", String.valueOf(clickCnt));
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCnt++;
                if(clickCnt<0 ||clickCnt>12) clickCnt=0;
                setTextItem(clickCnt);
                Log.d("클릭수", String.valueOf(clickCnt));
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           talkBack("뒤로가기");

                                       }
                                   }

        );


        beaconInfo1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
                    @Override
                    public void onInit(int i) {
                        if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                            tts.setLanguage(Locale.KOREA);     //한국어로 설정
                            tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                            tts.speak(beaconInfo1TextView.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                });

            }
        });
        beaconInfo2TextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() { //tts구현
                    @Override
                    public void onInit(int i) {

                        if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                            tts.setLanguage(Locale.KOREA);     //한국어로 설정
                            tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                            tts.speak(beaconInfo2TextView.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                });

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
    //매대 안내를 위한 비콘 함수
    private void setBeaconItemInfo(String leftItem1,String rightItem2){
        //텍스트 안내
        beaconInfo1TextView.setText(leftItem1);
        beaconInfo2TextView.setText(rightItem2);
        //음성안내
        beaconInfo1TextView.setContentDescription("왼쪽에 "+leftItem1+"이 있습니다.");
        beaconInfo2TextView.setContentDescription("오른쪽에"+rightItem2+"이 있습니다.");
    }
    //편의시설 안내를 위한 비콘 함수
    private void setBeaconFacilitiesInfo(String facilities){
        //텍스트 안내
        beaconInfo1TextView.setText(facilities);
        beaconInfo2TextView.setText(facilities);
        //음성안내
        beaconInfo1TextView.setContentDescription("현재 위치는 " + facilities + "입니다.");
        beaconInfo1TextView.setContentDescription("현재 위치는 " + facilities + "입니다.");
    }
    //행사상품 안내를 위한 비콘 함수
    private void setBeaconSaleInfo(String saleItem1,String saleItem2){
        //텍스트 안내
        beaconInfo1TextView.setText(saleItem1);
        beaconInfo2TextView.setText(saleItem2);
        //음성안내
        beaconInfo1TextView.setContentDescription(saleItem1+" 있습니다.");
        beaconInfo2TextView.setContentDescription( saleItem2+" 있습니다.");
    }


}
