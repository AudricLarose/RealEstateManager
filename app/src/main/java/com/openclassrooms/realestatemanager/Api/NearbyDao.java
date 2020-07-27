package com.openclassrooms.realestatemanager.Api;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.modele.NearbyEstate;

import java.util.List;

@Dao
public interface NearbyDao {

    @Query("SELECT * FROM bddNearby")
    LiveData<List<NearbyEstate>> selectAllImage();

    @Query("SELECT * FROM bddNearby WHERE idEstate = :itemid")
    LiveData<List<NearbyEstate>> selectAllImageDeuxFois(int itemid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNearby(NearbyEstate task);

    @Update
    int upDateNearby(NearbyEstate task);

    @Query("DELETE FROM bddTable WHERE id = :itemid")
    int deleteNearby(long itemid);

    @Query("DELETE FROM bddTable")
    void DeleteAllNearby();

    @Query("DELETE FROM bddTable WHERE idEstate = :itemid")
    void DeletePArticularNearbysGroup(int itemid);
}
