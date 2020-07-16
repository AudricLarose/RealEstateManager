package com.openclassrooms.realestatemanager.Api;

import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;
import com.openclassrooms.realestatemanager.modele.NearbyEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.List;

public interface InterfaceRealEstate {
    List<RealEstate> getRealEstateList();

    List<ImagesRealEstate> getImageRealEstates();

    List<NearbyEstate> getNearbyEstates();

    List<RealEstate> getTempListInsert();

    List<RealEstate> getTempListUpdate();
}
