package com.openclassrooms.realestatemanager.modele;

import androidx.room.Relation;

import java.util.List;

public class ImageAndRealEstate {

    @Relation(parentColumn = "id", entityColumn = "idEstate", entity = ImagesRealEstate.class)
    public List<ImagesRealEstate> imagesRealEstateList;

    public ImageAndRealEstate(List<ImagesRealEstate> imagesRealEstateList) {
        this.imagesRealEstateList = imagesRealEstateList;
    }

    public List<ImagesRealEstate> getDepartments() {
        return imagesRealEstateList;
    }

    public void setDepartments(List<ImagesRealEstate> imagesRealEstateList) {
        this.imagesRealEstateList = imagesRealEstateList;
    }
}
