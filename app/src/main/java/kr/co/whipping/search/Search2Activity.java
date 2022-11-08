package kr.co.whipping.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.whipping.R;
import kr.co.whipping.search.db.AppDatabase;
import kr.co.whipping.search.db.Item;

public class Search2Activity extends AppCompatActivity {
    EditText searchEditTextview;
    private  ItemListAdapter itemListAdapter;
    private String searchText;
    private Button backButton;
    private Button goBackItemSearch;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);


        searchEditTextview =(EditText) findViewById(R.id.searchEditTextview);
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


        initRecyclerView();
        loadSearchItemList();
    }
    private void initRecyclerView(){
        RecyclerView recyclerView= findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        itemListAdapter = new ItemListAdapter(this);
        recyclerView.setAdapter(itemListAdapter);

    }
    private void loadSearchItemList(){
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        List<Item> searchItemList= db.itemDao().searchItems(searchText);
        itemListAdapter.setItemList(searchItemList);

    }

    private void saveNewItem(String itemName,String itemLocation){
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());

        Item item = new Item();
        item.item_name=itemName;
        item.item_location=itemLocation;
        db.itemDao().insertItem(item);
    }
    private void deleteAllItem(){
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());

        db.itemDao().deleteAllItems();
    }
}
