package kr.co.whipping.locationinfo;

import static java.lang.Math.pow;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import java.security.Permission;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import kr.co.whipping.R;
import kr.co.whipping.scan.barcordscan.BarcodeScanActivity;
import kr.co.whipping.scan.camerascan.CameraScanActivity;

public class BeaconMainActivity extends AppCompatActivity {

    private static final int REQUEST_ACCESS_FINE_LOCATION = 1000;
    private static final int REQUEST_ALL_PERMISSIONS = 2;
    private MinewBeaconManager mMinewBeaconManager;
    private boolean isScanning;

    private TextView beaconInfo1TextView;
    private TextView beaconInfo2TextView;
    private Button itemInfoBtn;
    private Button barcodeInfoBtn;
    private ImageView backBtn;
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


    private Queue<String>  disappearBeaconsNameQue= new LinkedList<>(); // disappearBeaconsNameQue;


    UserRssi comp = new UserRssi();
    private TextView mStart_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_mainactivity);

        initView();
        initManager();
        //모든 권한 확인하기
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if (!hasPermissions(this, PERMISSIONS_S_ABOVE)) {
                requestPermissions(PERMISSIONS_S_ABOVE, REQUEST_ALL_PERMISSIONS);
            }
            //권한이 없다면 권한 요구하기
        }else {
            if (!hasPermissions(this, PERMISSIONS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSIONS);
                }
            }
        }
        initListener();

    }

    private boolean hasPermissions(Context context, String[] permissions){
        for(String permission : permissions){
            if(ActivityCompat.checkSelfPermission(context,permission)
            !=PackageManager.PERMISSION_GRANTED){
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




    private void initView() {
        backBtn=(ImageView)findViewById(R.id.backBtn);
        beaconInfo1TextView = (TextView) findViewById(R.id.beaconInfo1TextView);
        beaconInfo2TextView = (TextView) findViewById(R.id.beaconInfo2TextView);
        itemInfoBtn = (Button) findViewById(R.id.itemInfoTextView);
        barcodeInfoBtn = (Button) findViewById(R.id.barcodeInfoBtn);
        itemInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraScanActivity.class);
                startActivity(intent);
            }
        });
        barcodeInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BarcodeScanActivity.class);
                startActivity(intent);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
                                   }

        );
    }

    //비콘 매니저 실행
    private void initManager() {
        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
    }

    //비콘 탐색 시작 버튼
    private void initListener() {
//        mStart_scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (mMinewBeaconManager != null) {
//                    BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
//                    switch (bluetoothState) { //블루투스 권한
//                        case BluetoothStateNotSupported:
//                            Toast.makeText(BeaconMainActivity.this, "Not Support BLE", Toast.LENGTH_SHORT).show();
//                            finish();
//                            break;
//                        case BluetoothStatePowerOff:
//                            return;
//                        case BluetoothStatePowerOn:
//                            break;
//                    }
//                }
//                if (isScanning) {
//                    isScanning = false;
//                    mStart_scan.setText("Start");
//                    if (mMinewBeaconManager != null) {
//                        mMinewBeaconManager.stopScan();
//                    }
//                } else {
//                    isScanning = true;
//                    mStart_scan.setText("Stop");
//                    try {
//                        mMinewBeaconManager.startScan(); //스캔시작
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        //위치 권한을 키자

        if (mMinewBeaconManager != null) {
                    BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
                    switch (bluetoothState) { //블루투스 권한
                        case BluetoothStateNotSupported:
                            Toast.makeText(BeaconMainActivity.this, "Not Support BLE", Toast.LENGTH_SHORT).show();
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
             *   if the manager find some new beacon, it will call back this method.
             *
             *  @param minewBeacons  new beacons the manager scanned
             */
            @Override
            public void onAppearBeacons(List<MinewBeacon> minewBeacons) {
                //Log.e("onAppearBeacons()","실행");



            }

            /**
             *  if a beacon didn't update data in 10 seconds, we think this beacon is out of rang, the manager will call back this method.
             *
             *  @param minewBeacons beacons out of range
             */
            @Override
            public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {
                if(!minewBeacons.isEmpty()) {
                    for (MinewBeacon minewBeacon : minewBeacons) {
                        String disappearBeaconName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                        if (disappearBeaconName.length() >= 6) {
                            if (disappearBeaconName.substring(0, 6).equals("beacon")) {
                                Log.e("사라진 비콘이름", disappearBeaconName);

                                if (disappearBeaconsNameQue.size() > 2) {
                                    disappearBeaconsNameQue.remove();
                                }
                                if (!disappearBeaconsNameQue.contains(disappearBeaconName)) {
                                    disappearBeaconsNameQue.offer(disappearBeaconName); //큐에 사라진 비콘 삽입
                                    Log.e("큐사이즈", String.valueOf(disappearBeaconsNameQue.size()));

                                }
                            }
                        }
                    }
                }

            }

            /**
             *  the manager calls back this method every 1 seconds, you can get all scanned beacons.
             *
             *  @param minewBeacons all scanned beacons
             */
            @Override
            public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
                if (!minewBeacons.isEmpty()) { //근처에 비콘이 없을 경우가 아니라면
                    String beaconName = minewBeacons.get(0).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    Log.e("가장가까운 비콘 이름", beaconName);
                    if (beaconName.length() >= 6) {
                        if (beaconName.substring(0, 6).equals("beacon")) {
                            //KalmanFilter kalmanFilter = new KalmanFilter();
                            //RSSI값으로 부터 거리를 구하기 위해서 제일 가까운 3개의 비콘의 RSSI값 받아오기
                            //                    double beaconRssi0 = minewBeacons.get(0).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getFloatValue();
                            //                    double beaconRssi1 = minewBeacons.get(1).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getFloatValue();
                            //                    double beaconRssi2 = minewBeacons.get(2).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getFloatValue();

                            //                    //제일 가까운 3개의 RSSI값 받아서 필터링 함수 적용
                            //                    beaconRssi0= kalmanFilter.filtering(beaconRssi0);
                            //                    beaconRssi1 = kalmanFilter.filtering(beaconRssi1);
                            //                    beaconRssi2=kalmanFilter.filtering(beaconRssi2);

                            //제일 가까운 비콘 3개의 이름 값 받기

                            //                    String beaconName1 = minewBeacons.get(1).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                            //                    String beaconName2 = minewBeacons.get(2).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();

                            Log.e("가장가까운 비콘 이름", beaconName);

                            //Log.e("beacon1",minewBeacons.get(1).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue());
                            //RSSI로 비콘으로 부터 거리계산하기
                            //                    double beaconDistance0 = calculateAccuracy(beaconRssi0);
                            //                    double beaconDistance1 = calculateAccuracy(beaconRssi1);
                            //                    double beaconDistance2 = calculateAccuracy(beaconRssi2);
                            //

                            //                    Log.d("비콘정보1: ",beaconName1+", 비콘1의 RSSI"+beaconRssi1+", 비콘1으로 부터 거리"+beaconDistance1);
                            //                    Log.d("비콘정보2: ",beaconName2+", 비콘2의 RSSI"+beaconRssi2+", 비콘2으로 부터 거리"+beaconDistance2);


                            //편의시설 정보 위치
                            if (beaconName.equals("beacon9")) {
                                Log.e("beacon9인식", "화면 문구 : 1층 엘레베이터, 음성안내문구 : 1층 엘레베이터 입니다.");
                                setBeaconFacilitiesInfo("1층 엘레베이터");
                            } else if (beaconName.equals("beaconA")) {
                                Log.e("beacon10인식", "화면 문구 : 1층 화장실, 음성안내문구 : 1층 화장실 입니다.");
                                setBeaconFacilitiesInfo("1층 화장실");
                            }

                            //시나리오 작성
                            //1. 매장입구
                            else if (beaconName.equals("beaconB")) {
                                Log.e("beaconB인식", "화면 문구 : 1층 매장입구, 음성안내문구 : 1층 매장입구 입니다.");
                                setBeaconFacilitiesInfo("1층 매장입구");
                            }
                            //2. 1층 계산대
                            else if (beaconName.equals("beaconC")) {
                                Log.e("beaconC인식", "화면 문구 : 1층 계산대, 음성안내문구 : 1층 계산대 입니다.");
                                setBeaconFacilitiesInfo("1층 계산대");
                            }
                            //3. 생활용품 / 스포츠매대
                            else if (beaconName.equals("beacon8")) {
                                //생활용품, 스포츠 매대를 인색했을 경우
                                if (disappearBeaconsNameQue.contains("beaconB") || disappearBeaconsNameQue.contains("beaconC"))
                                    //매장입구, 계산대 방향으로 오는 경우
                                    setBeaconItemInfo("생활용품", "스포츠");
                                else { //반대편
                                    setBeaconItemInfo("스포츠", "생활용품");
                                }
                            }
                            //4.헤어용품/ 구강용품 매대
                            if (beaconName.equals("beacon1")) {
                                if ((disappearBeaconsNameQue.contains("beaconC") || disappearBeaconsNameQue.contains("beacon8")))
                                    //생활용품,스포츠 매대 방향으로 오는 경우
                                    setBeaconItemInfo("헤어용품", "구강용품");
                                else {
                                    setBeaconItemInfo("구강용품", "헤어용품");
                                }
                            }

                            //5. 샴푸/면도기 매대
                            else if (beaconName.equals("beacon2") || beaconName.equals("beacon3")) {
                                if ((disappearBeaconsNameQue.contains("beacon1")) || (disappearBeaconsNameQue.contains("beacon8"))) {
                                    //헤어용품, 구강용품을 먼저 인식 했을 경우
                                    setBeaconItemInfo("샴푸", "면도기");
                                } else {
                                    // 구강용품, 헤어용품을 먼저 인식 했을 경우
                                    setBeaconItemInfo("면도기", "샴푸");
                                }
                            }
                            //6. 트린트먼트/의약제품 및 가그린 매대
                            else if (beaconName.equals("beacon4") || beaconName.equals("beacon5")) {
                                //헤어용품/구강용품을 먼저 인식했거나 샴푸/면도기를 먼저 인식했을 경우
                                if (!disappearBeaconsNameQue.isEmpty()) {
                                    if (disappearBeaconsNameQue.contains("beacon1") || disappearBeaconsNameQue.contains("beacon2")
                                            || disappearBeaconsNameQue.contains("beacon3"))
                                        setBeaconItemInfo("트리트먼트", "의약제품 및 가그린 ");
                                    else {
                                        setBeaconItemInfo("의약제품 및 가그린", "트리트먼트");
                                    }
                                }
                            }

                            //7. 트리트먼트/면도기
                            else if (beaconName.equals("beacon6") || beaconName.equals("beacon7")) {
                                //헤어용품, 구강용품을 먼저 인식했거나 트리트먼트/의약제품 및 가그린 먼저 인식했을 경우
                                if ((disappearBeaconsNameQue.contains("beacon1") || disappearBeaconsNameQue.equals("beacon4") || disappearBeaconsNameQue.equals("beacon5")))
                                    setBeaconItemInfo("트리트먼트", "면도기");
                                else {
                                    setBeaconItemInfo("면도기", "트리트먼트");
                                }
                            }

                            //8.  행사상품 안내
                            else if (beaconName.equals("beaconG")) {
                                setBeaconSaleInfo("추천 상품\n 리엔 물들임 트린트먼트150ml(흑갈색)", "1+1 행사상품\n 헤드앤숄더 샴푸850ml");
                            }

                            //9. 구강용품/헤어용품
                            else if (beaconName.equals("beaconF")) {
                                setBeaconItemInfo("구강용품", "헤어용품");
                            } else if (beaconName.equals("beaconE")) {
                                setBeaconFacilitiesInfo("2층으로 가는 에스컬레이터");
                            } else if (beaconName.equals("beaconD")) {
                                if (disappearBeaconsNameQue.contains("beacon15") || disappearBeaconsNameQue.contains("beacon16"))
                                    setBeaconItemInfo("생활용품", "가전");
                                else {
                                    setBeaconItemInfo("가전", "생활용품");
                                }
                            }
                        }
                    }

                }
            }



            /**
             *  the manager calls back this method when BluetoothStateChanged.
             *
             *  @param state BluetoothState
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

    //비콘 RSSI를 이용한 거리값 계산 함수
    //n은 경로 손실 지수로 주변 환경(벽 또는 장애물의 여부 등)에 따라 2~4의 값을 가집니다. 주변에 아무런 장애물도 없다면 경로 손실 지수는 2이며,
    // 장애물의 여부와 개수에 따라 3 또는 4가 될 수 있습니다. d는 거리이고, α는 TX power로 특정 거리에서 수신기로부터 측정된 기본 RSSI 값입니다.
    private static double calculateAccuracy(double rssi){
        if(rssi==1){return -1;}//정확도 알수 없음

        int n=2; //constant N
        int alpha = -63; //rssi at 1m

        return pow(10.0,((alpha-rssi)/(10*n)));
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
        beaconInfo1TextView.setContentDescription("추천 상품인"+ saleItem1+" 있습니다.");
        beaconInfo2TextView.setContentDescription("1+1 행사 중인"+ saleItem2+" 있습니다.");
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop scan
        if (isScanning) {
            mMinewBeaconManager.stopScan();
        }
    }


}
