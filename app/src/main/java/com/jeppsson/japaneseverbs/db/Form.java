package com.jeppsson.japaneseverbs.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "forms")
public class Form {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="form_id")
    public long id;

    public boolean dictionaryForm;

    public String txt;

    @Override
    public String toString() {
        return Form.class.getSimpleName() + " id: " + id + " txt: " + txt;
    }
}
