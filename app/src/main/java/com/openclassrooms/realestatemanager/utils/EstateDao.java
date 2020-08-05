package com.openclassrooms.realestatemanager.utils;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;

@Dao
public abstract class EstateDao {
    @Query("SELECT * FROM bdd WHERE id = :userId")
    public abstract Cursor getItemsWithCursor(long userId);

    @Query("SELECT * FROM bdd ")
    public abstract LiveData<List<RealEstate>> selectAllEstate();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertEstate(RealEstate task);

    @Update
    public abstract int upDateEstate(RealEstate task);

    @Query("DELETE FROM bdd WHERE id = :itemid")
    public abstract int deleteEstate(long itemid);

    @Query("DELETE FROM bdd")
    public abstract void DeleteAllEstate();

    @Query("SELECT DISTINCT bdd.*, bddImage.idEstate, bddImage.image,bddImage.descriptionImage,bddImage.linkFb,bddNearby.idEstate, bddNearby.nearby FROM bdd " +
            "LEFT OUTER JOIN bddImage ON  bdd.id= bddImage.idEstate" +
            " LEFT OUTER JOIN bddNearby ON bdd.id=bddNearby.idEstate " +
            "where  (bdd.town like COALESCE(:town, town))" +
            "AND (bdd.prix BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 100000000000))" +
            "AND (bdd.surface BETWEEN COALESCE(:minSurface, 0) AND COALESCE(:maxSurface, 10000000))" +
            "AND (bdd.chambre >= COALESCE(:minNbRoom, 0)) " +
            "AND (bdd.piece >= COALESCE(:minNbBedrooms, 0)) " +
            "AND (bdd.sdb >= COALESCE(:minNbBathrooms, 0)) " +
            "AND bdd.numberPhotos>=COALESCE(:count, 0)" +
            "AND (bdd.nomAgent like COALESCE(:agentName, nomAgent))" +
            "AND (bdd.ischecked like COALESCE(:binear, ischecked ))" +
            "AND strftime('%s', market) BETWEEN strftime('%s', :date) AND  '2060-01-20'" +
            " GROUP BY bdd.id "
    )

    public abstract LiveData<List<RealEstate>> selectAllEstateSorted(String town, Integer minPrice, Integer maxPrice, Integer minSurface, Integer maxSurface,
                                                                     Integer minNbRoom, Integer minNbBedrooms, Integer minNbBathrooms, int count, String agentName, String binear, String date);




    @Query("SELECT DISTINCT bdd.*, bddImage.idEstate, bddImage.image,bddImage.descriptionImage,bddImage.linkFb,bddNearby.idEstate, bddNearby.nearby FROM bdd " +
            "LEFT JOIN bddImage ON  bdd.id= bddImage.idEstate" +
            " LEFT JOIN bddNearby ON bddNearby.idEstate = bdd.id " +
            "where  (bdd.town like COALESCE(:town, town))" +
            "AND (bdd.prix BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 100000000000))" +
            "AND (bdd.surface BETWEEN COALESCE(:minSurface, 0) AND COALESCE(:maxSurface, 10000000))" +
            "AND (bdd.chambre >= COALESCE(:minNbRoom, 0)) " +
            "AND (bdd.piece >= COALESCE(:minNbBedrooms, 0)) " +
            "AND  (bdd.sdb >= COALESCE(:minNbBathrooms, 0)) " +
            "AND bdd.numberPhotos>=COALESCE(:count, 0)" +
            "AND (bdd.nomAgent like COALESCE(:agentName, nomAgent))" +
            "AND (bdd.ischecked like COALESCE(:binear, ischecked ))" +
            "AND strftime('%s', market) BETWEEN strftime('%s', :start_date) AND  '2060-01-20'" +
            "AND (nearby IN (:listnearby))" +
            " GROUP BY bdd.id "
    )

    public abstract LiveData<List<RealEstate>> selectAllEstateSortedListNEarby(String town, Integer minPrice, Integer maxPrice, Integer minSurface, Integer maxSurface,
                                                                               Integer minNbRoom, Integer minNbBedrooms, Integer minNbBathrooms, int count, String agentName, String binear, String start_date, List<String> listnearby);

    @Query("SELECT DISTINCT bdd.*, bddImage.idEstate, bddImage.image,bddImage.descriptionImage,bddImage.linkFb,bddNearby.idEstate, bddNearby.nearby FROM bdd " +
            "LEFT JOIN bddImage ON  bdd.id= bddImage.idEstate" +
            " LEFT JOIN bddNearby ON bddNearby.idEstate = bdd.id " +
            "where strftime('%s', ischecked)BETWEEN COALESCE(strftime('%s', :datef),'1999-01-01')AND '2060-01-20'")
    public abstract LiveData<List<RealEstate>> selectAllEstateSortediselled(String datef);

    @Query("SELECT DISTINCT bdd.*, bddImage.idEstate, bddImage.image,bddImage.descriptionImage,bddImage.linkFb,bddNearby.idEstate, bddNearby.nearby FROM bdd " +
            "LEFT OUTER JOIN bddImage ON  bdd.id= bddImage.idEstate" +
            " LEFT OUTER JOIN bddNearby ON bddNearby.idEstate = bdd.id ")
    public abstract LiveData<List<RealEstate>> selectAllEstateSortediselled();


    @Query("SELECT DISTINCT bdd.*, bddImage.idEstate, bddImage.image,bddImage.descriptionImage,bddImage.linkFb,bddNearby.idEstate, bddNearby.nearby FROM bdd " +
            "LEFT JOIN bddImage ON  bdd.id= bddImage.idEstate" +
            " LEFT JOIN bddNearby ON bddNearby.idEstate = bdd.id " +
            "where  (bdd.town like COALESCE(:town, town))" +
            "AND (bdd.prix BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 100000000000))" +
            "AND (bdd.surface BETWEEN COALESCE(:minSurface, 0) AND COALESCE(:maxSurface, 10000000))" +
            "AND (bdd.chambre >= COALESCE(:minNbRoom, 0)) " +
            "AND (bdd.piece >= COALESCE(:minNbBedrooms, 0)) " +
            "AND  (bdd.sdb >= COALESCE(:minNbBathrooms, 0)) " +
            "AND bdd.numberPhotos>=COALESCE(:count, 0)" +
            "AND (bdd.nomAgent like COALESCE(:agentName, nomAgent))" +
            "AND (bdd.ischecked like COALESCE(:binear, ischecked ))" +
            "AND strftime('%s', market) BETWEEN strftime('%s', :start_date) AND  '2060-01-20'" +
            "AND (type IN (:listType))" +
            " GROUP BY bdd.id " )

    public abstract LiveData<List<RealEstate>> selectAllEstateSortedListType(String town, Integer minPrice, Integer maxPrice, Integer minSurface, Integer maxSurface,
                                                                             Integer minNbRoom, Integer minNbBedrooms, Integer minNbBathrooms, int count, String agentName, String binear, String start_date, List<String> listType);

    @Query("SELECT DISTINCT bdd.*, bddImage.idEstate, bddImage.image,bddImage.descriptionImage,bddImage.linkFb,bddNearby.idEstate, bddNearby.nearby FROM bdd " +
            "LEFT JOIN bddImage ON  bdd.id= bddImage.idEstate" +
            " LEFT JOIN bddNearby ON bddNearby.idEstate = bdd.id " +
            "where  (bdd.town like COALESCE(:town, town))" +
            "AND (bdd.prix BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 100000000000))" +
            "AND (bdd.surface BETWEEN COALESCE(:minSurface, 0) AND COALESCE(:maxSurface, 10000000))" +
            "AND (bdd.chambre >= COALESCE(:minNbRoom, 0)) " +
            "AND (bdd.piece >= COALESCE(:minNbBedrooms, 0)) " +
            "AND  (bdd.sdb >= COALESCE(:minNbBathrooms, 0)) " +
            "AND bdd.numberPhotos>=COALESCE(:count, 0)" +
            "AND (bdd.nomAgent like COALESCE(:agentName, nomAgent))" +
            "AND (bdd.ischecked like COALESCE(:binear, ischecked ))" +
            "AND strftime('%s', market) BETWEEN strftime('%s', :start_date) AND  '2060-01-20'" +
            "AND (type IN (:listType))" +
            "AND (nearby IN (:listNearby))" +
            " GROUP BY bdd.id ")

    public abstract LiveData<List<RealEstate>> selectAllEstateSortedListTypeNEarbyToo(String town, Integer minPrice, Integer maxPrice, Integer minSurface, Integer maxSurface,
                                                                                      Integer minNbRoom, Integer minNbBedrooms, Integer minNbBathrooms, int count, String agentName, String binear, String start_date, List<String> listType, List<String> listNearby);
}
