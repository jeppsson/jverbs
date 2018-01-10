package com.jeppsson.japaneseverbs.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface VerbDao {

    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insertForm(Form form);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertVerb(Verb form);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    long insertVerbBase(VerbBase verbBase);

    @Query("SELECT verbs.meaning as meaning, forms.txt as form, "
            + "verbs.kanji as kanji, verbs.romanji as romanji, verbs.furigana as furigana "
            + "FROM verbs "
            + "INNER JOIN verbs_base ON base_id = tbl_verb_base_id "
            + "INNER JOIN forms ON form_id = tbl_form_id")
    LiveData<List<Verb2>> loadAllVerbs();

    @Query("SELECT verbs.meaning as meaning, verbs.tbl_verb_base_id as id, verbs.furigana as furigana, "
            + "verbs.kanji as kanji, verbs.romanji as romanji "
            + "FROM verbs "
            + "INNER JOIN forms ON form_id = tbl_form_id "
            + "WHERE forms.dictionaryForm = 1")
    LiveData<List<Verb3>> loadDictionaryVerbs();

    @Query("SELECT verbs.meaning as meaning, forms.txt as form, "
            + "verbs.kanji as kanji, verbs.romanji as romanji, verbs.furigana as furigana "
            + "FROM verbs "
            + "INNER JOIN forms ON form_id = tbl_form_id "
            + "WHERE tbl_verb_base_id = :verbId")
    LiveData<List<Verb2>> loadVerb(long verbId);

    @Query("DELETE FROM verbs")
    void clearVerbs();

    @Query("DELETE FROM forms")
    void clearForms();

    @Query("DELETE FROM verbs_base")
    void clearBase();
}
