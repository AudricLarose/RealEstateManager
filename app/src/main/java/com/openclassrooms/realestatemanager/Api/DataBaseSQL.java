package com.openclassrooms.realestatemanager.Api;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;
import com.openclassrooms.realestatemanager.modele.NearbyEstate;
import com.openclassrooms.realestatemanager.utils.EstateDao;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.openclassrooms.realestatemanager.utils.ImageDao;

@Database(entities = {RealEstate.class, ImagesRealEstate.class, NearbyEstate.class}, version = 46, exportSchema = false )
public abstract class DataBaseSQL extends RoomDatabase {
    private static volatile DataBaseSQL instance;
    public abstract EstateDao estateDao();
    public abstract ImageDao imageDao();
    public abstract NearbyDao nearbyDao();

    public static synchronized DataBaseSQL getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DataBaseSQL.class, "bdd.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}




