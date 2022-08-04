package kr.co.whipping.scan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.whipping.R;
import kr.co.whipping.databinding.ActivityScanBinding;
import kr.co.whipping.scan.barcordscan.BarcodeScanActivity;
import kr.co.whipping.scan.camerascan.CameraScanActivity;

public class ScanActivity extends AppCompatActivity {
    private ActivityScanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.itemscanBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), CameraScanActivity.class);
                startActivity(intent);
            }
        });
        binding.barcordscanBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(), BarcodeScanActivity.class);
                startActivity(intent);
            }
        });
    }

}