package kr.co.whipping.search.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Item {


    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name ="item_name")
    public String item_name;

    @ColumnInfo(name = "item_location")
    public String item_location;
}
