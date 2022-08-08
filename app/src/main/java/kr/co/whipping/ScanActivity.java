package kr.co.whipping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.co.whipping.scan.barcordscan.BarcodeScanActivity;

public class ScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Button barcodeRecog = (Button) findViewById(R.id.barcordscanBtn);
        barcodeRecog.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), BarcodeScanActivity.class);
                startActivity(intent);
            }
        });
    }
}