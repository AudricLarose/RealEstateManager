package com.openclassrooms.realestatemanager.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.DataBaseSQL;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;
import com.openclassrooms.realestatemanager.modele.NearbyEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Philippe on 21/02/2018.
 */
public class Utils {
    public int count = 0;

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param dollars
     * @return
     */
    public static int convertDollarToEuro(int dollars) {
        return (int) Math.round(dollars * 0.812);
    }

    public static int convertEuroToDollar(int euros) {
        return (int) Math.round(euros / 0.812);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @return
     */

    public static String getDateFormat(Context context, Calendar c) {
        return DateUtils.formatDateTime(context, c.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
    }

    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date());
    }

    public static String getDollarFormat(int number2Convert) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

        return format.format(number2Convert);

    }

    public static String getEuroFormat(int converted) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        return format.format(converted);
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param context
     * @return
     */
    public static Boolean isInternetAvailableo(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    // Verify if user have GPS on
    public static void GPSOnVerify(Context context, GPSCallBAck gpsCallBAck) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertGPSInactif(context);
        } else {
            gpsCallBAck.onRetrieve();
        }
    }

    // Show Notification if GPS is off
    private static void AlertGPSInactif(Context context) {
        AlertDialog alertDialog = AlertGPS(context);
        alertDialog.show();
    }

    private static AlertDialog AlertGPS(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.input_gps, null);
        builder.setView(view).setPositiveButton(context.getString(R.string.gpssetting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                Utils.GPSOnVerify(context, new GPSCallBAck() {
                    @Override
                    public void onRetrieve() {

                    }
                });

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }

    @IntRange(from = 0, to = 3)
    public static int getConnectionType(Context context) {
        int result = 0;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = getResultIfVersionIsNewer(result, cm);
        } else {
            result = getResultIfVersionIsOlder(result, cm);
        }
        return result;
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static int getResultIfVersionIsNewer(int result, ConnectivityManager cm) {
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = 3;
                }
            }
        }
        return result;
    }

    @SuppressWarnings({"ALL", "deprecation"})
    private static int getResultIfVersionIsOlder(int result, ConnectivityManager cm) {
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    result = 2;
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    result = 1;
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_VPN) {
                    result = 3;
                }
            }
        }
        return result;
    }

    // Verify if user have Internet is on and stop if not
    public static boolean internetOnVerify(Context context) {
        int connection = getConnectionType(context);
        if (connection == 1 || connection == 2) {
            return true;
        } else {
            return false;
        }
    }


    public static List<RealEstate> sortedbyPriceCroissant(List<RealEstate> listeRealEstate) {
        Collections.sort(listeRealEstate, new Comparator<RealEstate>() {
            @Override
            public int compare(RealEstate o1, RealEstate o2) {
                if (Integer.valueOf(o1.getPrix()) > Integer.valueOf(o2.getPrix())) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
        return listeRealEstate;
    }

    public static List<RealEstate> sortedbyPriceDecroissant(List<RealEstate> listeRealEstate) {
        try {
            Collections.sort(listeRealEstate, new Comparator<RealEstate>() {
                @Override
                public int compare(RealEstate o1, RealEstate o2) {
                    if (Integer.valueOf(o1.getPrix()) > Integer.valueOf(o2.getPrix())) {
                        return -1;
                    } else {
                        return +1;
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listeRealEstate;
    }

    public static String linkBuilder(LatLng latLng) {
        String linkTransformed = "https://maps.googleapis.com/maps/api/staticmap?zoom=15&size=300x300&maptype=roadmap&markers=color:red%7Clabel:C%7C " + latLng.latitude + "," + latLng.longitude + "&key=" + "AIzaSyC_WkswyFXkxGjNsC4Ie_zoMbA5WuiRR68";
        return linkTransformed;
    }

    public static void findAddress(Context context, String adress, AdressGenerators retroAction) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(adress, 1);
            if (addressList != null && addressList.size() > 0) {
                retroAction.onSuccess(addressList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void takeDataInBDD(final CallBackInterfaceForBDD callBackInterfaceForBDD) {
        ExtendedServiceEstate servicePlace = DI.getService();
        final List<RealEstate> listeRealEstate = servicePlace.getRealEstateList();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("realestate")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<RealEstate> resultsBDD = null;
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments().size() > 0) {
                            resultsBDD = queryDocumentSnapshots.toObjects(RealEstate.class);
                            callBackInterfaceForBDD.onFinishEstate(resultsBDD, e);

                        } else {
                            callBackInterfaceForBDD.onFail();

                        }
                    }
                });
    }
    public static void takeDataImageInBDD(final CallBackInterfaceForBDDImage callBackInterfaceForBDD) {
        ExtendedServiceEstate servicePlace = DI.getService();
        final List<ImagesRealEstate> listeRealEstate = servicePlace.getImageRealEstates();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("imageEstate")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<ImagesRealEstate> resultsBDD = null;
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments().size() > 0) {
                            resultsBDD = queryDocumentSnapshots.toObjects(ImagesRealEstate.class);
                            callBackInterfaceForBDD.onFinishImage(resultsBDD, e);
                        } else {
                            callBackInterfaceForBDD.onFail();

                        }
                    }
                });
    }
    public static void takeDataNEarbyInBDD(final CallBackInterfaceForBDDNearbu callBackInterfaceForBDD) {
        ExtendedServiceEstate servicePlace = DI.getService();
        final List<NearbyEstate> listeRealEstate = servicePlace.getNearbyEstates();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("nearbyestates")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<NearbyEstate> resultsBDD = null;
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments().size() > 0) {
                            resultsBDD = queryDocumentSnapshots.toObjects(NearbyEstate.class);
                            callBackInterfaceForBDD.onFinishNearby(resultsBDD, e);

                        } else {
                            callBackInterfaceForBDD.onFail();

                        }
                    }
                });
    }

    public static void sendItToMyBDDatRealEstate(RealEstate estate) {
        Map note = new HashMap();
        note.put("id", estate.getId());
        note.put("type", estate.getType());
        note.put("nomAgent", estate.getNomAgent());
        note.put("adresse", estate.getAdresse());
        note.put("ischecked", estate.getIschecked());
        note.put("postal", estate.getPostal());
        note.put("town", estate.getTown());
        note.put("description", estate.getDescription());
        note.put("chambre", estate.getChambre());
        note.put("piece", estate.getPiece());
        note.put("sdb", estate.getSdb());
        note.put("surface", estate.getSurface());
        note.put("market", estate.getMarket());
        note.put("prix", estate.getPrix());
        note.put("lattitude", estate.getLattitude());
        note.put("longitude", estate.getLongitude());
        note.put("url", estate.getUrl());
        note.put("inEuro", estate.getInEuro());
        note.put("selled", estate.getSelled());

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("realestate").document(String.valueOf(estate.hashCode())).set(note);
    }

    public static void upDateMyBDDPlease(RealEstate estate, RealEstate realEstate) {
        Map note = new HashMap();
        note.put("id", estate.getId());
        note.put("type", estate.getType());
        note.put("nomAgent", estate.getNomAgent());
        note.put("adresse", estate.getAdresse());
        note.put("ischecked", estate.getIschecked());
        note.put("postal", estate.getPostal());
        note.put("town", estate.getTown());
        note.put("description", estate.getDescription());
        note.put("chambre", estate.getChambre());
        note.put("piece", estate.getPiece());
        note.put("sdb", estate.getSdb());
        note.put("surface", estate.getSurface());
        note.put("market", estate.getMarket());
        note.put("prix", estate.getPrix());
        note.put("lattitude", estate.getLattitude());
        note.put("longitude", estate.getLongitude());
        note.put("url", estate.getUrl());
        note.put("inEuro", estate.getInEuro());
        note.put("selled", estate.getSelled());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("realestate").document(String.valueOf(estate.getId())).update(note);
    }

    public static void sendMyBDDImagePlease(ImagesRealEstate imagesRealEstate) {
        Map note = new HashMap();
        note.put("id", imagesRealEstate.getId());
        note.put("descriptionImage", imagesRealEstate.getDescriptionImage());
        note.put("idEstate", imagesRealEstate.getIdEstate());
        note.put("linkFb", imagesRealEstate.getLinkFb());
        note.put("image", imagesRealEstate.getImage());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("imageEstate").document(String.valueOf(imagesRealEstate.getId())).set(note);
    }

    public static void upDateMyBDDImagePlease(ImagesRealEstate imagesRealEstate) {
        Map note = new HashMap();
        note.put("id", imagesRealEstate.getId());
        note.put("descriptionImage", imagesRealEstate.getDescriptionImage());
        note.put("idEstate", imagesRealEstate.getIdEstate());
        note.put("linkFb", imagesRealEstate.getLinkFb());
        note.put("image", imagesRealEstate.getImage());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("imageEstate").document(String.valueOf(imagesRealEstate.getId())).update(note);
    }

    public static void sendMyBDDNearbyPlease(NearbyEstate nearbyEstate) {
        Map note = new HashMap();
        note.put("id", nearbyEstate.getId());
        note.put("idEstate", nearbyEstate.getIdEstate());
        note.put("nearby", nearbyEstate.getNearby());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("nearbyestates").document(String.valueOf(nearbyEstate.getId())).set(note);
    }

    public static void upDateMyBDDNearbyPlease(NearbyEstate nearbyEstate) {
        Map note = new HashMap();
        note.put("id", nearbyEstate.getId());
        note.put("idEstate", nearbyEstate.getIdEstate());
        note.put("nearby", nearbyEstate.getNearby());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("nearbyestates").document(String.valueOf(nearbyEstate.getId())).update(note);
    }

    public static List<String> uploadImage(final RealEstate estate, final List<String> imagesRealEstateList, final Context context, final CallBackImage callBackImage) throws Exception {
        final List<String> urlList = new ArrayList<>();
        final DataBaseSQL dataBaseSQL= DataBaseSQL.getInstance(context);
        final int[] count = {1};
        for (int i = 0; i < imagesRealEstateList.size(); i++) {
            final String[] url = {""};
            final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(String.valueOf(estate.hashCode())).child("" + i);
            UploadTask uploadTask = mStorageRef.putFile(Uri.parse(imagesRealEstateList.get(i)));
            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Le fichier n'est pas envoyé car la connexion est mauvaise", Toast.LENGTH_SHORT).show();
                    }
                    return mStorageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        url[0] = task.getResult().toString();
                        urlList.add(url[0]);
                    }
                    if (count[0] == imagesRealEstateList.size()) {
                        callBackImage.onFinish(urlList);
                    } else {
                        count[0] = count[0] + 1;
                    }
                }
            });
        }
        return urlList;
    }

    @SuppressLint("ResourceAsColor")
    public static void buttonBlocker(Button button) {
        button.setEnabled(false);
        button.setBackgroundColor(R.color.colorPrimary);
    }

    public interface CallBackImage {
        void onFinish(List<String> s);
    }

    public interface CallBackInterfaceForBDD {
        void onFinishEstate(List<RealEstate> realEstateList, FirebaseFirestoreException e);
        void onFail();
    }
    public interface CallBackInterfaceForBDDImage {
        void onFinishImage(List<ImagesRealEstate> imagesRealEstateList, FirebaseFirestoreException e);
        void onFail();
    }
    public interface CallBackInterfaceForBDDNearbu {

        void onFinishNearby(List<NearbyEstate> nearbyEstateList, FirebaseFirestoreException e);
        void onFail();
    }

    public interface GPSCallBAck {
        void onRetrieve();
    }

    public interface AdressGenerators {
        void onSuccess(List<Address> addressList);
    }
}

