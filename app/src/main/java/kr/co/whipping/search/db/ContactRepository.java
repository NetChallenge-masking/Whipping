package kr.co.whipping.search.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ContactRepository {
    private ItemDao ItemDao;
    private LiveData<List<Item>> allItems;

    public ContactRepository(Application application) {
        AppDatabase db = AppDatabase.getDbInstance(application);
        ItemDao = db.itemDao();
        allItems = ItemDao.getAll();
    }

    public LiveData<List<Item>> getAllItems() { return allItems; }
    public void insert(Item item) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            ItemDao.insertItem(item);
        });
    }
}