package kr.co.whipping.scan.barcordscan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import kr.co.whipping.R;

public class BarcodeScanActivity extends AppCompatActivity {

    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        new IntentIntegrator(this).initiateScan();

//        IntentIntegrator integrator = new IntentIntegrator(this);
//        integrator.initiateScan();
    }
//    public void onClick(View V) {
//        IntentIntegrator integrator = new IntentIntegrator(this);
//        integrator.initiateScan();
//    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String barcodenum;
            String barcodetype;


            barcodenum = scanResult.getContents();
            barcodetype = scanResult.getFormatName();

            Button minus = (Button) findViewById(R.id.minus);
            Button plus = (Button) findViewById(R.id.plus);
            Button cancel = (Button) findViewById(R.id.cancel);
            Button add = (Button) findViewById(R.id.add);
            TextView prodCount = (TextView) findViewById(R.id.count);

            TextView category = (TextView) findViewById(R.id.category);
            TextView nameOfprod = (TextView) findViewById(R.id.nameOfProd);
            TextView price = (TextView) findViewById(R.id.price);

            //DB에서 상품 정보 가져오는 코드 추가
            //임시로 코드로 가져옴
            if (barcodenum.equals("4902430232159")){
                category.setText("샴푸");
                nameOfprod.setText("헤드&숄더 두피 토탈 솔루션 가려운 두피케어");
                price.setText("15,900");
                }
            else if (barcodenum.equals("8801046361252")){
                category.setText("트리트먼트");
                nameOfprod.setText("케라시스 데미지 클리닉 오리지널 컨디셔너 린스 1,000ml ");
                price.setText("7,900");
            }
            else if (barcodenum.equals("8801008700372")){
                category.setText("가그린");
                nameOfprod.setText("리스테린 그린티 마일드 750ml");
                price.setText("6,930");
            }
            else if (barcodenum.equals("8809685832560")){
                category.setText("트리트먼트");
                nameOfprod.setText("살롱10 트리트먼트 250ml (손상모발)");
                price.setText("12,900");
            }
            else if (barcodenum.equals("8809539444581")){
                category.setText("트리트먼트");
                nameOfprod.setText("미장센 트리트먼트 1000ml (스무디 실키)");
                price.setText("7,490");
            }
            else if (barcodenum.equals("4902430896689")){
                Log.i("면도기", "면도기 인식");
//
                category.setText("면도기");
                nameOfprod.setText("질레트 마하5스포츠 면도날 8입");
                price.setText("31,900");
            }




//            TextView etBarcode = (TextView) findViewById(R.id.etBarcode);
//            TextView etTyp = (TextView) findViewById(R.id.etTyp);
//            etBarcode.setText(barcodenum);
//            etTyp.setText(barcodetype);

            ImageView img_barcode;
            img_barcode = (ImageView)findViewById(R.id.img_barcode) ;

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            final int WIDTH = 180;
            final int HEIGHT = 90;

            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(barcodenum, BarcodeFormat.valueOf(barcodetype), WIDTH, HEIGHT);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                img_barcode.setImageBitmap(bitmap);
            } catch (Exception e) {
            }


            //플러스, 마이너스
            plus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    count++;
                    prodCount.setText((count+""));
                }
            });
            minus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    count--;
                    prodCount.setText((count+""));
                }
            });

            //취소
            cancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    onBackPressed();
                }
            });

            //담기
            add.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //
                }
            });
        }
    }
}
