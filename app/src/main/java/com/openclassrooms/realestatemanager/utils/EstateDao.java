package com.openclassrooms.realestatemanager.utils;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;

    @Dao
    public interface EstateDao {
        @Query("SELECT * FROM bdd WHERE id = :userId")
        Cursor getItemsWithCursor(long userId);

        @Query("SELECT * FROM bdd ")
        LiveData<List<RealEstate>> selectAllEstate();

        @Insert
        long insertEstate(RealEstate task);

        @Update
        int upDateEstate(RealEstate task);

        @Query("DELETE FROM bdd WHERE id = :itemid")
        int deleteEstate(long itemid);

        @Query("DELETE FROM bdd")
        void DeleteAllEstate();

        @Query("SELECT * FROM bdd " +
//                "INNER JOIN bddtable ON  bdd.id= bddTable.idEstate" +
//                " INNER JOIN bddNearby ON bddNearby.idEstate = bdd.id " +
                "where  (town like COALESCE(:town, town))" +
                "AND (prix BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 100000000000))" +
                "AND (surface BETWEEN COALESCE(:minSurface, 0) AND COALESCE(:maxSurface, 10000000))"+
                "AND (chambre >= COALESCE(:minNbRoom, 0)) " +
                "AND (piece >= COALESCE(:minNbBedrooms, 0)) " +
                "AND  (sdb >= COALESCE(:minNbBathrooms, 0)) " +
                "AND (nomAgent like COALESCE(:agentName, nomAgent))" +
                "AND  ((SELECT count(*) from bdd)= (:count))" )

                LiveData<List<RealEstate>> selectAllEstateSorted(String town, Integer minPrice, Integer maxPrice, Integer minSurface, Integer maxSurface,
                                                                 Integer minNbRoom, Integer minNbBedrooms, Integer minNbBathrooms, String agentName,int count);
    }

