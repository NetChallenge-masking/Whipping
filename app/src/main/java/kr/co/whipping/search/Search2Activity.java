package kr.co.whipping.search;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.whipping.Basket;
import kr.co.whipping.BasketAdapter;
import kr.co.whipping.CartActivity;
import kr.co.whipping.DBHelper;
import kr.co.whipping.R;

public class Search2Activity extends AppCompatActivity {
    EditText searchEditTextview;
    private  ItemListAdapter adapter;
    private String searchText;
    private Button backButton;
    private Button goBackItemSearch;
    RecyclerView recyclerView;
    DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        ArrayList<Item> itemList = new ArrayList<>();

        searchEditTextview = (EditText) findViewById(R.id.searchEditTextview);
        backButton=findViewById(R.id.btn_itemsearch_back);
        goBackItemSearch = findViewById(R.id.btn_goback_itemsearch);

        Intent intent =getIntent();
        searchText=intent.getExtras().getString("searchItemName").toString();
        searchEditTextview.setText(searchText);

        goBackItemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new ItemListAdapter(itemList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Search2Activity.this));

        dbHelper = new DBHelper(Search2Activity.this);

        Cursor cursor = dbHelper.readItemLocation(searchEditTextview.getText().toString());
        while (cursor.moveToNext()) {
            Item item = new Item(
                    cursor.getString(0),  //item_name
                    cursor.getString(1)  //item_location
            );
            itemList.add(item);

            adapter.notifyDataSetChanged();
        }

    }
}
