package com.openclassrooms.realestatemanager.Api;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.openclassrooms.realestatemanager.utils.EstateDao;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.openclassrooms.realestatemanager.utils.EstateDao_Impl;

@Database(entities = {RealEstate.class}, version = 16, exportSchema = false )
public abstract class DataBaseSQL extends RoomDatabase {
    private static  DataBaseSQL instance;
    public abstract EstateDao estateDao();

    public static synchronized DataBaseSQL getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DataBaseSQL.class, "bdd")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}




