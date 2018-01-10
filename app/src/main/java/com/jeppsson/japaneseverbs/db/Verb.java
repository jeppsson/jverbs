package com.jeppsson.japaneseverbs.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "verbs",
        foreignKeys = {
                @ForeignKey(
                        entity = VerbBase.class,
                        parentColumns = "base_id",
                        childColumns = "tbl_verb_base_id"),
                @ForeignKey(
                        entity = Form.class,
                        parentColumns = "form_id",
                        childColumns = "tbl_form_id")
        })
public class Verb {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "tbl_form_id")
    public long formId;

    @ColumnInfo(name = "tbl_verb_base_id")
    public long verbBaseId;

    public String meaning;

    public String kanji;

    public String furigana;

    public String romanji;
}
