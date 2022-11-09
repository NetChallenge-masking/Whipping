package kr.co.whipping.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.whipping.R;

public class Search2Activity extends AppCompatActivity {
    EditText searchEditTextview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation1);

        ImageView wanttogo = findViewById(R.id.wanttogo);
        Animation anim = AnimationUtils.loadAnimation
                (getApplicationContext(), R.anim.updown);
        wanttogo.startAnimation(anim);
    }

       // searchEditTextview =(EditText) findViewById(R.id.searchEditTextview);
        //Intent intent =getIntent();
        //searchEditTextview.setText(intent.getExtras().getString("searchItemName").toString());


   // }
}
