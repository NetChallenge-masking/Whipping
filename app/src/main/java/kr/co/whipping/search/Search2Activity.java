package kr.co.whipping.search;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.whipping.R;

public class Search2Activity extends AppCompatActivity {
    EditText searchEditTextview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        searchEditTextview =(EditText) findViewById(R.id.searchEditTextview);
        Intent intent =getIntent();
        searchEditTextview.setText(intent.getExtras().getString("searchItemName").toString());


    }
}
