package com.openclassrooms.realestatemanager.Api;

import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;
import com.openclassrooms.realestatemanager.modele.NearbyEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public  abstract class ListGenerator {

    public static List<RealEstate> realEstateList = Arrays.asList(

    );
    public static List<RealEstate> getRealEstateList() {
        return new ArrayList<>(realEstateList);
    }


    public static List<RealEstate> temp = Arrays.asList(

    );
    public static List<RealEstate> tempUpdate = Arrays.asList(

    );
    public static List<RealEstate> getTempList() {
        return new ArrayList<>(temp);
    }

    public static List<RealEstate> getTempListUpdate() {
        return new ArrayList<>(tempUpdate);
    }

    public static List<ImagesRealEstate> imagesRealEstates = Arrays.asList(

    );
    public static List<ImagesRealEstate> getImagesRealEstates() {
        return new ArrayList<>(imagesRealEstates);
    }
    public static List<NearbyEstate> nearbyRealEstates = Arrays.asList(

    );
    public static List<NearbyEstate> getNearbyRealEstates() {
        return new ArrayList<>(nearbyRealEstates);
    }
}

