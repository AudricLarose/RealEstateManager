package com.openclassrooms.realestatemanager.activity;

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
import android.widget.ProgressBar;
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

import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.DataBaseSQL;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.dummy.ItemDetailActivity;
import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;
import com.openclassrooms.realestatemanager.modele.NearbyEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.openclassrooms.realestatemanager.utils.Adaptateur;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.utils.Utils.CallBackInterfaceForBDD;

import java.util.ArrayList;
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
    private Adaptateur adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean amIInEuro = true;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView textNotif;
    private DataBaseSQL database;
    private ProgressBar progressBar;


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
        deployProgressBar();
        setSupportActionBar(toolbar);
        setTitle(R.string.Welcome);
        initiateDataBaseSQL();
        detailsIfTablet();
        resultsActivityIfEstateExist();
    }

    private void deployProgressBar() {
        progressBar = findViewById(R.id.progress_bar_main);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void resultsActivityIfEstateExist() {
        List<RealEstate> realEstateList = grabEstatFromSearchActivity();
        if (realEstateList != null && realEstateList.size() > 0) {
            adapter = new Adaptateur(Utils.sortedbyPriceDecroissant(realEstateList), mTwoPane, this, this);
            recyclerView = findViewById(R.id.RecyclerviewEstate);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(MainActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        } else {
            NoExistingEstateAction();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void NoExistingEstateAction() {
        takeDataInBDDIfInternetIsHere();
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
                majNotif();
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
                takeDataInBDDIfInternetIsHere();
                deployRecyclerView();
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
            public void onFinishNearby(final List<NearbyEstate> nearbyEstateList) {
                if (listTemp.size() == 0 || listTempUpdate.size() == 0 && nearbyEstateList.size() > 0) {
                    database.nearbyDao().DeleteAllNearby();
                    for (int i = 0; i < nearbyEstateList.size(); i++) {
                        if (nearbyEstateList.get(i).getNearby() != null) {
                            database.nearbyDao().insertNearby(nearbyEstateList.get(i));
                        }
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
            public void onFinishImage(List<ImagesRealEstate> imagesRealEstateList) {
                if (listTemp.size() == 0 || listTempUpdate.size() == 0 && imagesRealEstateList.size() > 0) {
                    database.imageDao().DeleteAllEstate();
                    for (int i = 0; i < imagesRealEstateList.size(); i++) {
                        if (imagesRealEstateList.get(i).getImage() != null) {
                            database.imageDao().insertEstate(imagesRealEstateList.get(i));
                        }
                    }
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
            public void onFinishEstate(List<RealEstate> realEstateList) {

                if (listTemp.size() == 0 || listTempUpdate.size() == 0 && realEstateList.size() > 0) {
                    database.estateDao().DeleteAllEstate();
                    for (int i = 0; i < realEstateList.size(); i++) {
                        if (realEstateList.get(i).getPrix() != null && realEstateList.get(i).getTown() != null && realEstateList.get(i).getType() != null) {
                            database.estateDao().insertEstate(realEstateList.get(i));
                        }
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
        sendTempInsertFileToFireB(listTemp);
    }

    private void sendTempInsertFileToFireB(final List<RealEstate> listTempE) {
        if (listTempE.size() == 0) {
            if (listTempUpdate.size() != 0) {
                sendTempUpdateFileToFireB(listTempUpdate);
            } else {
                progressBar.setVisibility(View.GONE);
            }
            Toast.makeText(this, R.string.aucunmail, Toast.LENGTH_SHORT).show();
        } else {
            if (Utils.internetOnVerify(this)) {
                if (listTempE.size() > 0) {
                    final DataBaseSQL dataBaseSQL = DataBaseSQL.getInstance(this);
                    for (int i = 0; i < listTempE.size(); i++) {
                        Utils.sendItToMyBDDatRealEstate(listTempE.get(i));
                        final List<String> photos = new ArrayList<>();
                        final List<String> link = new ArrayList<>();
                        dataBaseSQL.imageDao().selectAllImageinParticular(listTempE.get(i).getId()).observe(this, new Observer<List<ImagesRealEstate>>() {
                            @Override
                            public void onChanged(List<ImagesRealEstate> imagesRealEstateList) {
                                if (imagesRealEstateList.size() > 0) {
                                    for (int j = 0; j < imagesRealEstateList.size(); j++) {
                                        photos.add(imagesRealEstateList.get(j).getImage());
                                    }
                                    try {
                                        Utils.uploadImage(listTempE.get(listTempE.size() - 1), photos, MainActivity.this, new Utils.CallBackImage() {
                                            @Override
                                            public void onFinish(List<String> s) {
                                                link.addAll(s);
                                                send2nearby(dataBaseSQL, listTempE);
                                                send2Image(dataBaseSQL, link, listTempE);
                                                if (listTempE.size() != 0) {
                                                    listTempE.remove(listTempE.size() - 1);
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        send2nearby(dataBaseSQL, listTempE);
                                        send2Image(dataBaseSQL, link, listTempE);
                                        if (listTempE.size() != 0) {
                                            listTempE.remove(listTempE.size() - 1);
                                        }
                                    }
                                } else {
                                    send2nearby(dataBaseSQL, listTempE);
                                    send2Image(dataBaseSQL, link, listTempE);
                                    if (listTempE.size() != 0) {
                                        listTempE.remove(listTempE.size() - 1);
                                    }
                                }
                            }
                        });
                        listTempE.get(i).setTempInsert("False");
                        database.estateDao().upDateEstate(listTempE.get(i));
                    }
                    Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
                    majNotif();
                } else {
                    if (listTempUpdate.size() > 0) {
                        sendTempUpdateFileToFireB(listTempUpdate);
                    }
                }
            } else {
                Toast.makeText(this, R.string.ni_internet, Toast.LENGTH_LONG).show();
            }
        }
    }

    interface CallBack {
        void onFinish();
    }

    private void send2ImageUpdate(final DataBaseSQL dataBaseSQL, final List<String> link, final List<RealEstate> listTempUp, final CallBack callBack) {
        if (listTempUp.size() > 0 && link.size() > 0) {
            Utils.eraseDataImageInBDD(listTempUp.get(listTempUp.size() - 1).getId(), new Utils.CallbackErase() {
                @Override
                public void onFinish() {
                    if (listTempUp.size() > 0 && link.size() > 0) {
                        dataBaseSQL.imageDao().selectAllImageinParticular(listTempUp.get(listTempUp.size() - 1).getId()).observe(MainActivity.this, new Observer<List<ImagesRealEstate>>() {
                            @Override
                            public void onChanged(List<ImagesRealEstate> imagesRealEstateList) {
                                for (int j = 0; j < imagesRealEstateList.size(); j++) {
                                    imagesRealEstateList.get(j).setLinkFb(link.get(0));
                                    Utils.sendMyBDDImagePlease(imagesRealEstateList.get(j));
                                }
                                callBack.onFinish();
                            }
                        });
                    }
                }
            });
        }
        ;
    }

    private void send2Image(DataBaseSQL dataBaseSQL, final List<String> link, List<RealEstate> listTempE) {
        if (listTempE.size() > 0 && link.size() > 0) {
            dataBaseSQL.imageDao().selectAllImageinParticular(listTempE.get(listTempE.size() - 1).getId()).observe(this, new Observer<List<ImagesRealEstate>>() {
                @Override
                public void onChanged(List<ImagesRealEstate> imagesRealEstateList) {
                    for (int j = 0; j < imagesRealEstateList.size(); j++) {
                        imagesRealEstateList.get(j).setLinkFb(link.get(0));
                        Utils.sendMyBDDImagePlease(imagesRealEstateList.get(j));
                    }
                }
            });
        }
    }

    private void send2nearby(DataBaseSQL dataBaseSQL, List<RealEstate> listTempE) {
        if (listTempE.size() > 0) {
            dataBaseSQL.nearbyDao().selectAllNearbyCondition(listTempE.get(listTempE.size() - 1).getId()).observe(this, new Observer<List<NearbyEstate>>() {
                @Override
                public void onChanged(List<NearbyEstate> nearbyEstateList) {
                    for (int j = 0; j < nearbyEstateList.size(); j++) {
                        Utils.sendMyBDDNearbyPlease(nearbyEstateList.get(j));
                    }
                }
            });
        }
    }

    private void send2nearbyUpdate(final DataBaseSQL dataBaseSQL, final List<RealEstate> listTempE) {
        if (listTempE.size() > 0) {
            Utils.eraseDataNEarbyInBDD(listTempE.get(listTempE.size() - 1).getId(), new Utils.CallbackErase() {
                @Override
                public void onFinish() {
                    if (listTempE.size() > 0) {
                        dataBaseSQL.nearbyDao().selectAllNearbyCondition(listTempE.get(listTempE.size() - 1).getId()).observe(MainActivity.this, new Observer<List<NearbyEstate>>() {
                            @Override
                            public void onChanged(List<NearbyEstate> nearbyEstateList) {
                                for (int j = 0; j < nearbyEstateList.size(); j++) {
                                    Utils.sendMyBDDNearbyPlease(nearbyEstateList.get(j));
                                }
                            }
                        });
                    }
                }
            });

        }
    }

    private void sendTempUpdateFileToFireB(final List<RealEstate> listTempA) {

        if (listTempA.size() == 0) {
            Toast.makeText(this, R.string.aucunmail, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else {
            if (Utils.internetOnVerify(this)) {
                progressBar.setVisibility(View.VISIBLE);
                if (listTempA.size() > 0) {
                    final DataBaseSQL dataBaseSQL = DataBaseSQL.getInstance(this);
                    for (int i = 0; i < listTempA.size(); i++) {
                        Utils.sendItToMyBDDatRealEstate(listTempA.get(i));
                        final List<String> photos = new ArrayList<>();
                        final List<String> link = new ArrayList<>();
                        dataBaseSQL.imageDao().selectAllImageinParticular(listTempA.get(i).getId()).observe(this, new Observer<List<ImagesRealEstate>>() {
                            @Override
                            public void onChanged(List<ImagesRealEstate> imagesRealEstateList) {
                                if (imagesRealEstateList.size() > 0) {
                                    for (int j = 0; j < imagesRealEstateList.size(); j++) {
                                        photos.add(imagesRealEstateList.get(j).getImage());
                                    }
                                    try {
                                        send2nearbyUpdate(dataBaseSQL, listTempA);
                                        Utils.uploadImage(listTempA.get(listTempA.size() - 1), photos, MainActivity.this, new Utils.CallBackImage() {
                                            @Override
                                            public void onFinish(List<String> s) {
                                                link.addAll(s);
                                                send2ImageUpdate(dataBaseSQL, link, listTempA, new CallBack() {
                                                    @Override
                                                    public void onFinish() {
                                                        if (listTempA.size() != 0) {
                                                            listTempA.remove(listTempA.size() - 1);
                                                        }
                                                        progressBar.setVisibility(View.GONE);

                                                    }
                                                });


                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        send2nearbyUpdate(dataBaseSQL, listTempA);
                                        send2ImageUpdate(dataBaseSQL, link, listTempA, new CallBack() {
                                            @Override
                                            public void onFinish() {
                                                if (listTempA.size() != 0) {
                                                    listTempA.remove(listTempA.size() - 1);
                                                }
                                            }
                                        });
                                        progressBar.setVisibility(View.GONE);

                                    }
                                } else {
                                    send2nearbyUpdate(dataBaseSQL, listTempA);
                                    send2ImageUpdate(dataBaseSQL, link, listTempA, new CallBack() {
                                        @Override
                                        public void onFinish() {
                                            if (listTempA.size() != 0) {
                                                listTempA.remove(listTempA.size() - 1);
                                            }
                                        }
                                    });
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                        listTempA.get(i).setTempUpdate("false");

                    }
                    Toast.makeText(this, R.string.sent, Toast.LENGTH_SHORT).show();
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
        majNotif();
    }
}
