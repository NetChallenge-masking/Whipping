package kr.co.whipping.search.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM item")
    List<Item> getAllItems();

    @Query("SELECT * FROM item ORDER BY item_name ASC")
    LiveData<List<Item>> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertItem(Item... items);

    @Delete
    void delete(Item item);

    @Query("DELETE FROM item")
    void deleteAllItems();


    @Query("SELECT * FROM item WHERE item_name LIKE :searchQuery")
    List<Item> searchItems(String searchQuery);


}
