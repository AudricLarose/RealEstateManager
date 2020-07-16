package com.openclassrooms.realestatemanager.utils;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.modele.ImageAndRealEstate;
import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;

@Dao
public interface ImageDao {

    @Query("SELECT * FROM bddTable")
    LiveData<List<ImagesRealEstate>> selectAllImage();

    @Query("SELECT * FROM bddTable WHERE idEstate = :itemid")
    LiveData<List<ImagesRealEstate>> selectAllImageDeuxFois(int itemid);

    @Insert
    long insertEstate(ImagesRealEstate task);

    @Update
    int upDateEstate(ImagesRealEstate task);

    @Query("DELETE FROM bddTable WHERE id = :itemid")
    int deleteEstate(long itemid);

    @Query("DELETE FROM bddTable")
    void DeleteAllEstate();

    @Query("DELETE FROM bddTable WHERE idEstate = :itemid")
    void DeletePArticularEstatesGroup(int itemid);
}
