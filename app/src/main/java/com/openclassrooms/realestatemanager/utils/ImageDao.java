package com.openclassrooms.realestatemanager.utils;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;

import java.util.List;

@Dao
public interface ImageDao {

    @Query("SELECT * FROM bddImage")
    LiveData<List<ImagesRealEstate>> selectAllImage();

    @Query("SELECT * FROM bddImage WHERE idEstate = :itemid")

    LiveData<List<ImagesRealEstate>> selectAllImageinParticular(int itemid);

    @Query("SELECT count(bddImage.image) from bddImage ")
    int selectAllImageNumbered();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEstate(ImagesRealEstate task);

    @Update
    int upDateEstate(ImagesRealEstate task);

    @Query("DELETE FROM bddImage WHERE id = :itemid")
    int deleteEstate(long itemid);

    @Query("DELETE FROM bddImage")
    void DeleteAllEstate();

    @Query("DELETE FROM bddImage WHERE idEstate = :itemid")
    void DeletePArticularEstatesGroup(int itemid);
}
