package com.sanxynet.journalapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;


@Database(entities = {Journal.class,Category.class},version = 1,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class JournalDatabase extends RoomDatabase {

    public static final String DB_NAME = "journal_db";
    private static JournalDatabase instance;
    private static Object LOCK = new Object();

    public static JournalDatabase getInstance(Context context){
        if (instance == null){
            synchronized (LOCK){
                if (instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(),JournalDatabase.class,DB_NAME)
                            .build();
                }
            }
        }
        return instance;
    }

    abstract JournalDao journalDao();
    abstract CategoryDao categoryDao();
}
