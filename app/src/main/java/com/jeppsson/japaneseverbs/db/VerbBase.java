package com.jeppsson.japaneseverbs.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "verbs_base")
public class VerbBase {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "base_id")
    public long baseId;

    public int verbId;
}
