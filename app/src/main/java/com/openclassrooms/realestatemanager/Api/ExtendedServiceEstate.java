package com.openclassrooms.realestatemanager.Api;

import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;
import com.openclassrooms.realestatemanager.modele.NearbyEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;

public class ExtendedServiceEstate implements InterfaceRealEstate{
    private List<RealEstate> realEstates = ListGenerator.getRealEstateList();
    private List<RealEstate> realtemplistInsert = ListGenerator.getTempList();
    private List<RealEstate> realtemplistUpdate = ListGenerator.getTempListUpdate();
    public List<RealEstate> getRealEstateList() {
        return realEstates;
    }
    private List<NearbyEstate> nearbyEstates = ListGenerator.getNearbyRealEstates();
    private List<ImagesRealEstate> imageRealEstates = ListGenerator.getImagesRealEstates();
    @Override
    public List<ImagesRealEstate> getImageRealEstates() {
        return imageRealEstates;
    }
    @Override
    public List<NearbyEstate> getNearbyEstates() {
        return nearbyEstates;
    }
    @Override
    public List<RealEstate> getTempListInsert() {
        return realtemplistInsert;
    }
    @Override
    public List<RealEstate> getTempListUpdate() {
        return realtemplistUpdate;
    }

}
