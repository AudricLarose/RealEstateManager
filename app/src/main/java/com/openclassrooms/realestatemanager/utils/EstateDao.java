package com.openclassrooms.realestatemanager.utils;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.Date;
import java.util.List;

@Dao
public abstract class EstateDao {
    @Query("SELECT * FROM bdd WHERE id = :userId")
    public abstract Cursor getItemsWithCursor(long userId);

    @Query("SELECT * FROM bdd ")
    public abstract LiveData<List<RealEstate>> selectAllEstate();

    @Insert
    public abstract long insertEstate(RealEstate task);

    @Update
    public abstract int upDateEstate(RealEstate task);

    @Query("DELETE FROM bdd WHERE id = :itemid")
    public abstract int deleteEstate(long itemid);

    @Query("DELETE FROM bdd")
    public abstract void DeleteAllEstate();

    @Query("SELECT * FROM bdd " +
            "LEFT JOIN bddtable ON  bdd.id= bddTable.idEstate" +
            " LEFT JOIN bddNearby ON bddNearby.idEstate = bdd.id " +
            "where  (bdd.town like COALESCE(:town, town))" +
            "AND (bdd.prix BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 100000000000))" +
            "AND (bdd.surface BETWEEN COALESCE(:minSurface, 0) AND COALESCE(:maxSurface, 10000000))" +
            "AND (bdd.chambre >= COALESCE(:minNbRoom, 0)) " +
            "AND (bdd.piece >= COALESCE(:minNbBedrooms, 0)) " +
            "AND  (bdd.sdb >= COALESCE(:minNbBathrooms, 0)) " +
            "AND (SELECT count(*) from bddTable)>=COALESCE(:count, 0)" +
            "AND (bdd.nomAgent like COALESCE(:agentName, nomAgent))" +
            "AND (bdd.selled like COALESCE(:binear, selled ))"
    )
    public abstract LiveData<List<RealEstate>> selectAllEstateSorted(String town, Integer minPrice, Integer maxPrice, Integer minSurface, Integer maxSurface,
                                                                     Integer minNbRoom, Integer minNbBedrooms, Integer minNbBathrooms, int count, String agentName, String binear);

    @Query("SELECT * FROM bdd " +
            "LEFT JOIN bddtable ON  bdd.id= bddTable.idEstate" +
            " LEFT JOIN bddNearby ON bddNearby.idEstate = bdd.id " +
            "where (type IN (:listType))")
    public abstract LiveData<List<RealEstate>> selectIfSelectTypeIsZero(String listType);

    @Query("SELECT * FROM bdd " +
            "LEFT JOIN bddtable ON  bdd.id= bddTable.idEstate" +
            " LEFT JOIN bddNearby ON bddNearby.idEstate = bdd.id " +
            "where (nearby IN (:listnearby))")
    public abstract LiveData<List<RealEstate>> selectIfSelectNearbyIsZero(List<String> listnearby);

    @Transaction
    public void SelectlistNearbyIsNotEmptyCase(String town, Integer minPrice, Integer maxPrice, Integer minSurface, Integer maxSurface,
                                             Integer minNbRoom, Integer minNbBedrooms, Integer minNbBathrooms, int count, String agentName, String binear,List<String> listnearby) {
        // Anything inside this method runs in a single transaction.
       // selectAllEstateSorted(town, minPrice, maxPrice, minSurface, maxSurface,minNbRoom, minNbBedrooms, minNbBathrooms,count,agentName, binear);
        selectIfSelectNearbyIsZero(listnearby);
    }
    @Transaction
    public void SelectlistNearbyIsNotEmptyCaseAndTypeToo(String town, Integer minPrice, Integer maxPrice, Integer minSurface, Integer maxSurface,
                                               Integer minNbRoom, Integer minNbBedrooms, Integer minNbBathrooms, int count, String agentName, String binear,List<String> listnearby,String listType) {
        // Anything inside this method runs in a single transaction.
     //   selectAllEstateSorted(town, minPrice, maxPrice, minSurface, maxSurface,minNbRoom, minNbBedrooms, minNbBathrooms,count,agentName, binear);
        selectIfSelectNearbyIsZero(listnearby);
        selectIfSelectTypeIsZero(listType);
    }
    @Transaction
    public void SelectlistTypeNotEmpty(String town, Integer minPrice, Integer maxPrice, Integer minSurface, Integer maxSurface,
                                               Integer minNbRoom, Integer minNbBedrooms, Integer minNbBathrooms, int count, String agentName, String binear,String listType) {
        // Anything inside this method runs in a single transaction.
     //   selectAllEstateSorted(town, minPrice, maxPrice, minSurface, maxSurface,minNbRoom, minNbBedrooms, minNbBathrooms,count,agentName, binear);
        selectIfSelectTypeIsZero(listType);
    }

}
