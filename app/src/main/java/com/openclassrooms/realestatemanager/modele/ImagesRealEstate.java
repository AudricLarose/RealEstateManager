package com.openclassrooms.realestatemanager.modele;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "bddImage", foreignKeys = @ForeignKey(entity = RealEstate.class,
        parentColumns = "id",
        childColumns = "idEstate",onDelete = ForeignKey.CASCADE))
public class ImagesRealEstate {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "idEstate")
    private int idEstate;

    private String descriptionImage;
    private String image;
    private String linkFb;

    public ImagesRealEstate() {
    }

    @Ignore
    public ImagesRealEstate(int idEstate, String descriptionImage, String image, String linkFb) {
        this.idEstate = idEstate;
        this.descriptionImage = descriptionImage;
        this.image = image;
        this.linkFb = linkFb;
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

    public String getDescriptionImage() {
        return descriptionImage;
    }

    public void setDescriptionImage(String descriptionImage) {
        this.descriptionImage = descriptionImage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLinkFb() {
        return linkFb;
    }

    public void setLinkFb(String linkFb) {
        this.linkFb = linkFb;
    }
}
