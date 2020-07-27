package com.openclassrooms.realestatemanager.dummy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.DataBaseSQL;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.activity.AddInformationActivity;
import com.openclassrooms.realestatemanager.activity.MapsActivity;
import com.openclassrooms.realestatemanager.activity.SearchActivity;
import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;
import com.openclassrooms.realestatemanager.modele.NearbyEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.openclassrooms.realestatemanager.utils.Adaptateur;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.utils.Utils.CallBackInterfaceForBDD;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity {

    int countLoup = 0;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private List<RealEstate> listTemp = serviceEstate.getTempListInsert();
    private List<RealEstate> listTempUpdate = serviceEstate.getTempListUpdate();
    private List<ImagesRealEstate> imagesRealEstateListes= serviceEstate.getImageRealEstates();
    private Adaptateur adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean amIInEuro = true;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView textNotif;
    private DataBaseSQL database;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermission() {
        if ((MainActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = null;
            LocationListener locationListener = null;
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.Welcome);
        initiateDataBaseSQL();
        detailsIfTablet();
        deployementButtonCancel();
        resultsActivityIfEstateExist();
        testResultSqlRequet();
    }

    private void testResultSqlRequet() {
        List<String> listTest = new ArrayList<>();
        DataBaseSQL database = DataBaseSQL.getInstance(this);
        caseIfListsAreEmpty();
    }

    private void caseIfListsAreEmpty() {
        DataBaseSQL database = DataBaseSQL.getInstance(this);
        List<String> nearby= new ArrayList<>();
        List<String> type= new ArrayList<>();
        String datef="";
        if (datef.trim().isEmpty()){
            datef=null;
        }
        if (nearby.isEmpty()){
            if (type.isEmpty()){
                LiveData<List<RealEstate>> datalist = database.estateDao().selectAllEstateSorted(null, null, null, null,
                        null, null, null, null, 0,null,null,"2019-01-10");

                datalist.observe(this, new Observer<List<RealEstate>>() {
                    @Override
                    public void onChanged(List<RealEstate> realEstateList) {
                        Toast.makeText(MainActivity.this, "" + realEstateList.size(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                LiveData<List<RealEstate>> datalist = database.estateDao().selectAllEstateSortedListType(null, null, null, null,
                        null, null, null, null, 0,null,null,"01/01/10",type,datef);

                datalist.observe(this, new Observer<List<RealEstate>>() {
                    @Override
                    public void onChanged(List<RealEstate> realEstateList) {
                        Toast.makeText(MainActivity.this, "" + realEstateList.size(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            if (type.isEmpty()){
                LiveData<List<RealEstate>> datalist = database.estateDao().selectAllEstateSortedListNEarby(null, null, null, null,
                        null, null, null, null, 0,null,null,"01/01/10",nearby,datef);

                datalist.observe(this, new Observer<List<RealEstate>>() {
                    @Override
                    public void onChanged(List<RealEstate> realEstateList) {
                        Toast.makeText(MainActivity.this, "" + realEstateList.size(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                LiveData<List<RealEstate>> datalist = database.estateDao().selectAllEstateSortedListTypeNEarbyToo(null, null, null, null,
                        null, null, null, null, 0,null,null,"01/01/10",type,nearby,datef);

                datalist.observe(this, new Observer<List<RealEstate>>() {
                    @Override
                    public void onChanged(List<RealEstate> realEstateList) {
                        Toast.makeText(MainActivity.this, "" + realEstateList.size(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void resultsActivityIfEstateExist() {
        List<RealEstate> realEstateList = grabEstatFromSearchActivity();
        if (realEstateList != null && realEstateList.size() > 0) {
            Toast.makeText(this, "" + realEstateList.size(), Toast.LENGTH_SHORT).show();
            adapter = new Adaptateur(Utils.sortedbyPriceDecroissant(realEstateList), mTwoPane, this, this);
            recyclerView = findViewById(R.id.RecyclerviewEstate);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(MainActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            initiateButtonCAncel().setVisibility(View.VISIBLE);
        } else {
            NoExistingEstateAction();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void NoExistingEstateAction() {
        takeDataInBDDIfInternetIsHere();
        takeImageInSQLITE();
        DeploytempHandler();
        deployementButtonAdd();
        deployementButtonMail();
        onSwipeToRefresh();
        Utils.internetOnVerify(this);
        deployementNotificationMail();
        askPermission();
        saveDataInSQLITE();
        deployRecyclerView();

    }

    private void initiateDataBaseSQL() {
        database = DataBaseSQL.getInstance(this);
    }

    private void deployementButtonMail() {
        final ImageButton mailButton = initateButtonMail();
        activateButtonMail(mailButton);
    }

    private void activateButtonMail(ImageButton mailButton) {
        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeploytempHandler();
            }
        });
    }

    private ImageButton initateButtonMail() {
        return findViewById(R.id.noInternet);
    }

    private List<RealEstate> grabEstatFromSearchActivity() {
        List<RealEstate> estates = null;
        Intent intent = MainActivity.this.getIntent();
        Bundle extra = intent.getExtras();
        if (extra != null) {
            estates = (List<RealEstate>) extra.getSerializable("RealEstate");
        }
        return estates;
    }

    private void onSwipeToRefresh() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DeploytempHandler();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void takeDataInBDDIfInternetIsHere() {
        if (Utils.internetOnVerify(this)) {
            takeDataForEstates();
        }
    }

    private void takeDataForNearby() {
        Utils.takeDataNEarbyInBDD(new Utils.CallBackInterfaceForBDDNearbu() {
            @Override
            public void onFinishNearby(final List<NearbyEstate> nearbyEstateList, FirebaseFirestoreException e) {
                if (listTemp.size() == 0 || listTempUpdate.size() == 0 && nearbyEstateList.size() > 0) {
                    database.nearbyDao().DeleteAllNearby();
                    for (int i = 0; i < nearbyEstateList.size(); i++) {
                        database.nearbyDao().insertNearby(nearbyEstateList.get(i));
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.actualisation, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {

            }
        });
    }

    private void takeDataForImage() {
        Utils.takeDataImageInBDD(new Utils.CallBackInterfaceForBDDImage() {
            @Override
            public void onFinishImage(List<ImagesRealEstate> imagesRealEstateList, FirebaseFirestoreException e) {
                if (listTemp.size() == 0 || listTempUpdate.size() == 0 && imagesRealEstateList.size() > 0) {
                    database.imageDao().DeleteAllEstate();
                    for (int i = 0; i < imagesRealEstateList.size(); i++) {
                        database.imageDao().insertEstate(imagesRealEstateList.get(i));
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.actualisation, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
            }
        });
    }

    private void takeDataForEstates() {
        Utils.takeDataInBDD(new CallBackInterfaceForBDD() {
            @Override
            public void onFinishEstate(List<RealEstate> realEstateList, FirebaseFirestoreException e) {

                if (listTemp.size() == 0 || listTempUpdate.size() == 0 && realEstateList.size() > 0) {
                    database.estateDao().DeleteAllEstate();
                    for (int i = 0; i < realEstateList.size(); i++) {
                        database.estateDao().insertEstate(realEstateList.get(i));
                    }
                    takeDataForNearby();
                    takeDataForImage();
                } else {
                    Toast.makeText(MainActivity.this, R.string.actualisation, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail() {
                Toast.makeText(MainActivity.this, R.string.nonew, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deployRecyclerView() {
        adapter = new Adaptateur(Utils.sortedbyPriceDecroissant(listRealEstate), mTwoPane, this, this);
        recyclerView = findViewById(R.id.RecyclerviewEstate);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void saveDataInSQLITE() {
        DataBaseSQL database = DataBaseSQL.getInstance(this);
        LiveData<List<RealEstate>> datalist = database.estateDao().selectAllEstate();
        datalist.observe(this, new Observer<List<RealEstate>>() {
            @Override
            public void onChanged(List<RealEstate> realEstates) {
                listRealEstate.clear();
                listRealEstate.addAll(realEstates);
                searchForNotif(realEstates);
                deployRecyclerView();
            }
        });
    }

    private void takeImageInSQLITE() {
        DataBaseSQL database = DataBaseSQL.getInstance(this);
        LiveData<List<ImagesRealEstate>> datalist = database.imageDao().selectAllImage();
        LiveData<List<NearbyEstate>> datalist3 = database.nearbyDao().selectAllImage();
        datalist.observe(this, new Observer<List<ImagesRealEstate>>() {
            @Override
            public void onChanged(List<ImagesRealEstate> imagesRealEstates) {
                if (imagesRealEstates.size() > 0) {
                    //         Toast.makeText(MainActivity.this, "images "+imagesRealEstates.size(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        datalist3.observe(this, new Observer<List<NearbyEstate>>() {
            @Override
            public void onChanged(List<NearbyEstate> nearbyEstates) {
                if (nearbyEstates.size() > 0) {
                    //           Toast. (MainActivity.this, "nearby precise" + nearbyEstates.size(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void searchForNotif(List<RealEstate> realEstates) {
        countLoup = countLoup + 1;
        if (countLoup == 1) {
            listTemp.clear();
            for (int i = 0; i < realEstates.size(); i++) {
                if (realEstates.get(i).getTempInsert().equals("true")) {
                    listTemp.add(realEstates.get(i));
                    majNotif();
                }
                if (realEstates.get(i).getTempUpdate().equals("true")) {
                    listTempUpdate.add(realEstates.get(i));
                    majNotif();
                }

            }
        }
    }

    private void deployementNotificationMail() {
        iniatiateAndActivateNotifLayout();
    }

    private void iniatiateAndActivateNotifLayout() {
        textNotif = findViewById(R.id.cart_badge);
        majNotif();
    }

    private void majNotif() {
        if (textNotif != null) {
            textNotif.setText(String.valueOf(listTemp.size() + listTempUpdate.size()));
        }
    }

    private void DeploytempHandler() {
        sendTempInsertFileToFireB();
        sendTempUpdateFileToFireB();
    }

    private void sendTempInsertFileToFireB() {
        if (listTemp.size() == 0) {
            Toast.makeText(this, R.string.aucunmail, Toast.LENGTH_SHORT).show();
        } else {
            if (Utils.internetOnVerify(this)) {
                if (listTemp.size() > 0) {
                    final DataBaseSQL dataBaseSQL = DataBaseSQL.getInstance(this);
                    for (int i = 0; i < listTemp.size(); i++) {
                        Utils.sendItToMyBDDatRealEstate(listTemp.get(i));
                        final int finalI = i;
                        send2nearby(dataBaseSQL, i, finalI);
                        send2Image(dataBaseSQL, i, finalI);
                        final List<String> photos= new ArrayList<>();
                        final int finalI1 = i;
                        dataBaseSQL.imageDao().selectAllImageDeuxFois(listTemp.get(i).getId()).observe(this, new Observer<List<ImagesRealEstate>>() {
                            @Override
                            public void onChanged(List<ImagesRealEstate> imagesRealEstateList) {
                                for (int j = 0; j < imagesRealEstateList.size(); j++) {
                                    photos.add(imagesRealEstateList.get(finalI1).getImage());
                                }
                                try {
                                    Utils.uploadImage(listTemp.get(finalI1),photos, MainActivity.this, new Utils.CallBackImage() {
                                        @Override
                                        public void onFinish(List<String> s) {
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        listTemp.get(i).setTempInsert("False");
                        database.estateDao().upDateEstate(listTemp.get(i));
                    }
                    Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
                    if (listTempUpdate.size() > 0) {
                        sendTempUpdateFileToFireB();
                    }
                    listTemp.clear();
                    majNotif();
                }
            } else {
                Toast.makeText(this, R.string.ni_internet, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void send2Image(DataBaseSQL dataBaseSQL, int i, final int finalI) {
        dataBaseSQL.imageDao().selectAllImageDeuxFois(listTemp.get(i).getId()).observe(this, new Observer<List<ImagesRealEstate>>() {
            @Override
            public void onChanged(List<ImagesRealEstate> imagesRealEstateList) {
                for (int j = 0; j < imagesRealEstateList.size(); j++) {
                    Utils.sendMyBDDImagePlease(imagesRealEstateList.get(finalI));
                }
            }
        });
    }

    private void send2nearby(DataBaseSQL dataBaseSQL, int i, final int finalI) {
        dataBaseSQL.nearbyDao().selectAllImageDeuxFois(listTemp.get(i).getId()).observe(this, new Observer<List<NearbyEstate>>() {
            @Override
            public void onChanged(List<NearbyEstate> nearbyEstateList) {
                for (int j = 0; j < nearbyEstateList.size(); j++) {
                    Utils.sendMyBDDNearbyPlease(nearbyEstateList.get(finalI));
                }
            }
        });
    }

    private void sendTempUpdateFileToFireB() {
        if (listTempUpdate.size() == 0) {
            Toast.makeText(this, R.string.aucunmail, Toast.LENGTH_SHORT).show();
        } else {
            if (Utils.internetOnVerify(this)) {
                if (listTempUpdate.size() > 0) {
                    for (int i = 0; i < listTempUpdate.size(); i++) {
                        Utils.upDateMyBDDPlease(listTempUpdate.get(i), listTempUpdate.get(i));
                        listTempUpdate.get(i).setTempInsert("False");
                        database.estateDao().upDateEstate(listTempUpdate.get(i));
                    }
                    Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
                    listTempUpdate.clear();
                    majNotif();
                }
            } else {
                Toast.makeText(this, R.string.ni_internet, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void detailsIfTablet() {
        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
    }

    private void deployementButtonCancel() {
        final ImageButton cancelButton = initiateButtonCAncel();
        activateButtonCancel(cancelButton);
    }

    private void activateButtonCancel(final ImageButton cancelButton) {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                NoExistingEstateAction();
                cancelButton.setVisibility(View.GONE);
            }
        });
    }

    private ImageButton initiateButtonCAncel() {
        return findViewById(R.id.buttoncancelactivitymain);

    }

    private void deployementButtonAdd() {
        final ImageButton addButton = initiateButtonAdd();
        activateButtonAdd(addButton);
    }


    private ImageButton initiateButtonAdd() {
        return findViewById(R.id.buttonadd);
    }

    private void activateButtonAdd(ImageButton addButton) {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddInformationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.iconebarmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.maps:
                deployGoogleMap();
                return true;
            case R.id.search:
                deploySearchActivity();
                return true;
            case R.id.conversion:
                knowIfConvertToEuroOrDollar();
                return true;
            case R.id.subcroissant:
                List<RealEstate> realEstateListcroissant = Utils.sortedbyPriceCroissant(listRealEstate);
                adapter = new Adaptateur(realEstateListcroissant, mTwoPane, this, this);
                recyclerView.setAdapter(adapter);
                return true;
            case R.id.subDecroissant:
                List<RealEstate> realEstateListdecroissant = Utils.sortedbyPriceDecroissant(listRealEstate);
                adapter = new Adaptateur(realEstateListdecroissant, mTwoPane, this, this);
                recyclerView.setAdapter(adapter);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deploySearchActivity() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void deployGoogleMap() {
        Utils.GPSOnVerify(this, new Utils.GPSCallBAck() {
            @Override
            public void onRetrieve() {
                if (Utils.internetOnVerify(MainActivity.this)) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.providerinternet), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void knowIfConvertToEuroOrDollar() {
        if (amIInEuro) {
            convertEuroList();
            amIInEuro = false;
            changeMyEuroParameter();
        } else {
            convertDollarList();
            amIInEuro = true;
            changeMyEuroParameter();
        }
    }

    private void changeMyEuroParameter() {
        if (amIInEuro) {
            for (int i = 0; i < listRealEstate.size(); i++) {
                listRealEstate.get(i).setInEuro("true");
            }
        } else {
            for (int i = 0; i < listRealEstate.size(); i++) {
                listRealEstate.get(i).setInEuro("false");
            }
        }
    }

    private void convertDollarList() {
        for (int i = 0; i < listRealEstate.size(); i++) {
            int converted = Utils.convertDollarToEuro(Integer.valueOf(listRealEstate.get(i).getPrix()));
            listRealEstate.get(i).setPrix(converted);
        }
        deployRecyclerView();
    }

    private void convertEuroList() {
        for (int i = 0; i < listRealEstate.size(); i++) {
            int converted = Utils.convertEuroToDollar(Integer.valueOf(listRealEstate.get(i).getPrix()));
            listRealEstate.get(i).setPrix(converted);
        }
        deployRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SQLITactionSettings();
    }

    private void SQLITactionSettings() {
        takeDataInBDDIfInternetIsHere();
        saveDataInSQLITE();
        takeImageInSQLITE();
        majNotif();
    }
}
