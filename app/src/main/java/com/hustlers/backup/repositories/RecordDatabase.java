package com.hustlers.backup.repositories;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.hustlers.backup.models.Record;

@Database(entities = {Record.class}, version = 1, exportSchema = false)
public abstract class RecordDatabase extends RoomDatabase {
    public abstract RecordDao daoAccess ();
}