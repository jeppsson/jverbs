package com.jeppsson.japaneseverbs.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Verb.class, Form.class, VerbBase.class}, version = 1)
public abstract class VerbDatabase extends RoomDatabase {

    private static VerbDatabase INSTANCE;

    public abstract VerbDao verbDao();

    public static VerbDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    VerbDatabase.class, "verbs-db")
                    .build();
        }

        return INSTANCE;
    }
}
