package com.sanxynet.journalapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface JournalDao {

    @Query("SELECT * from Journal ORDER BY priority DESC")
    List<Journal> getAllTodo();

    @Query("SELECT * FROM Journal WHERE ID = :id")
    Journal getTodoById(long id);

    @Insert
    void insertInDb(Journal journal);

    @Update
    void updateDb(Journal journal);

    @Delete
    void deleteFromDb(Journal journal);
}
