package com.openclassrooms.realestatemanager.Api;

public class DI {
    private static ExtendedServiceEstate extendedServiceEstate = new ExtendedServiceEstate();

    public static ExtendedServiceEstate getService() {
        return extendedServiceEstate;
    }

    private static ExtendedServiceEstate extendedServiceImage = new ExtendedServiceEstate();

    public static ExtendedServiceEstate getServiceImage() {
        return extendedServiceImage;
    }

    private static ExtendedServiceEstate extendedServiceNnearby = new ExtendedServiceEstate();

    public static ExtendedServiceEstate getServiceNearby() {
        return extendedServiceNnearby;
    }
}
