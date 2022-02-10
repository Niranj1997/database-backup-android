package com.hustlers.backup.repositories;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hustlers.backup.models.Record;

import java.util.List;

@Dao
public interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll ( List<Record> record );

    @Query("SELECT * FROM record")
    List<Record> retrieveAll ();

    @Query("SELECT COUNT(*) FROM record")
    Integer retrieveCount ();

    @Query("DELETE FROM record")
    void deleteAll ();
}
