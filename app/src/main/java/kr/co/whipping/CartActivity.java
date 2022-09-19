package kr.co.whipping;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    DBHelper dbHelper;
    RecyclerView recyclerView;
    BasketAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ArrayList<Basket> basketList = new ArrayList<>();

        recyclerView = findViewById(R.id.RecyclerView);
        adapter = new BasketAdapter(basketList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));

        dbHelper = new DBHelper(CartActivity.this);

        Cursor cursor = dbHelper.readAllBasket();
        while(cursor.moveToNext()) {
            Basket basket = new Basket(
                    cursor.getInt(0),  //basket_id
                    cursor.getString(2),  //barcode_id
                    cursor.getString(3),  //barcode_type
                    cursor.getInt(4)  //amount
            );
            basketList.add(basket);

            adapter.notifyDataSetChanged();
        }

        //상품 삭제 버튼
        Button basket_delete_btn = findViewById(R.id.basket_delete_btn);
        basket_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Basket recyclerItem = adapter.getSelected();
                if (recyclerItem == null) {
                    Toast.makeText(CartActivity.this, "선택된 아이템이 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 선택한 item 삭제
                basketList.remove(recyclerItem);

                // List 반영
                final int checkedPosition = adapter.getCheckedPosition();
                adapter.notifyItemRemoved(checkedPosition);

                // 선택 항목 초기화
                adapter.clearSelected();

                //db에서 상품 제거
                dbHelper.deleteBasket(recyclerItem.getBasketId());
            }
        });
    }
}