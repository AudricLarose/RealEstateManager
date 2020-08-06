package com.openclassrooms.realestatemanager.modele;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "bddNearby", foreignKeys = @ForeignKey(entity = RealEstate.class,
        parentColumns = "id",
        childColumns = "idEstate",onDelete = ForeignKey.CASCADE))
public class NearbyEstate {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "idEstate")
    private int idEstate;

    private String nearby;

    public NearbyEstate() {
    }

    @Ignore
    public NearbyEstate(int idEstate, String nearby) {
        this.idEstate = idEstate;
        this.nearby = nearby;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEstate() {
        return idEstate;
    }

    public void setIdEstate(int idEstate) {
        this.idEstate = idEstate;
    }

    public String getNearby() {
        return nearby;
    }

    public void setNearby(String nearby) {
        this.nearby = nearby;
    }
}
