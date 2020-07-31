package com.openclassrooms.realestatemanager.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.DataBaseSQL;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;
import com.openclassrooms.realestatemanager.modele.NearbyEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.openclassrooms.realestatemanager.utils.AdaptateurImage;
import com.openclassrooms.realestatemanager.utils.DatePickerFragment;
import com.openclassrooms.realestatemanager.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddInformationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "ShowResult";
    private static final int IMAGE_REQUEST = 102;
    private static final int REQUESTCODEGALLERY = 101;
    private static boolean isItChecked = false;
    private static RealEstate estate;
    public DataBaseSQL dataBaseSQL;
    public Uri imageUri;
    List<MaterialTextField> editTextContainer = new ArrayList<>();
    private Chip Cecole, Cmagasin, Cmetro, CParc, Cbus;
    private TextInputLayout eAdress, ePostal, eVille, ePrix, eSurface, ePiece, eChambre, eSdb, eDescr;
    private TextView eMarket, edit_ontheSell;
    private Spinner spinnerChoicce, spinnerNom;
    private Switch switchVendu;
    private Button btnOk, btnCancel, btnModify;
    private RelativeLayout relativeLayoutSell;
    private List<String> resultsValidatedByUser = new ArrayList<>();
    private Map<String, String> globalResultEstate = new HashMap<>();
    private List<String> globalResult = new ArrayList<>();
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private List<RealEstate> tempListInsert = serviceEstate.getTempListInsert();
    private List<RealEstate> tempListUpdate = serviceEstate.getTempListUpdate();
    private List<ImagesRealEstate> imagesRealEstateList = serviceEstate.getImageRealEstates();
    private Uri imageURL;
    private Double lattitudeRealEState;
    private Double longitudeRealEState;
    private String url;
    private List<Chip> ChipesContainer = new ArrayList<>();
    private List<String> listPhotoRealistetate = new ArrayList<>();
    private List<String> descritpionImage = new ArrayList<>();
    private boolean CameraActivate;
    private List<String> link = new ArrayList<>();
    private ProgressBar progressBar;
    private boolean selled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_information);
        setTitleToAdapt();
        initiateEditText();
        deployeButton();
        initiateDataBaseSQL();
        deployeChipes();
        deployRelativeLayout();
        deploySpinner();
        iniatiateAndActivateSwitch();
        deployRecyclerView();
        deployProgressBar();
        deployModificationAction();
        actionfleche();
    }

    private void initiateDataBaseSQL() {
        dataBaseSQL = DataBaseSQL.getInstance(this);
    }

    private void deployProgressBar() {
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setTitleToAdapt() {
        if (verifyEstateExistAlreadyForModify()) {
            setTitle(R.string.modify);
        } else {
            setTitle(R.string.ajouter);
        }
    }

    private void deployRelativeLayout() {
        relativeLayoutSell = findViewById(R.id.RelativeSell);
    }

    private Boolean verifyEstateExistAlreadyForModify() {
        Intent intent = this.getIntent();
        Bundle extra = intent.getExtras();
        if (extra != null) {
            estate = (RealEstate) extra.getSerializable("RealEstate");
            return true;
        }
        return false;
    }

    private void actionfleche() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void iniatiateAndActivateSwitch() {
        switchVendu = findViewById(R.id.switch_vendu);
        switchVendu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    relativeLayoutSell.setVisibility(View.VISIBLE);
                    getTimeIfDateIsEmpty(edit_ontheSell);
                    isItChecked=true;
                } else {
                    relativeLayoutSell.setVisibility(View.GONE);
                    edit_ontheSell.setText(getString(R.string.date));
                    isItChecked=false;

                }
            }
        });
    }

    private void deploySpinner() {
        initiateSpinner();
        activeSpinner();
    }

    private void initiateSpinner() {
        spinnerChoicce = findViewById(R.id.type_bien);
        spinnerNom = findViewById(R.id.nom_agent);
    }

    private void activeSpinner() {
        spinnerChoicce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                globalResultEstate.put("TypeEstate", adapterView.getSelectedItem().toString());
                globalResult.add(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerNom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                globalResultEstate.put("nameEstate", adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void initiateEditText() {
        eAdress = findViewById(R.id.edit_adresse);
        eChambre = findViewById(R.id.edit_chambre);
        eDescr = findViewById(R.id.edit_descript);
        eMarket = findViewById(R.id.edit_onthemarket);
        ePiece = findViewById(R.id.edit_piece);
        ePostal = findViewById(R.id.edit_codepostal);
        ePrix = findViewById(R.id.edit_prix);
        eSdb = findViewById(R.id.edit_sdb);
        eSurface = findViewById(R.id.edit_surface);
        eVille = findViewById(R.id.edit_ville);
        edit_ontheSell = findViewById(R.id.edit_ontheSell);
        getTimeIfDateIsEmpty(eMarket);
    }

    private void saveEntryEditText() {
        globalResultEstate.put("Adresse", eAdress.getEditText().getText().toString());
        globalResultEstate.put("Chambre", eChambre.getEditText().getText().toString());
        globalResultEstate.put("Description", eDescr.getEditText().getText().toString());
        globalResultEstate.put("Postal", ePostal.getEditText().getText().toString());
        globalResultEstate.put("Piece", ePiece.getEditText().getText().toString());
        globalResultEstate.put("Prix", ePrix.getEditText().getText().toString());
        globalResultEstate.put("SDB", eSdb.getEditText().getText().toString());
        globalResultEstate.put("Surface", eSurface.getEditText().getText().toString());
        globalResultEstate.put("Ville", eVille.getEditText().getText().toString());
        globalResultEstate.put("dateSell", edit_ontheSell.getText().toString());
        globalResultEstate.put("date", eMarket.getText().toString());

    }

    private void deployeChipes() {
        ChipesContainer = initiateChipes();
        resultsValidatedByUser = activateChip(ChipesContainer);
    }

    private List<Chip> initiateChipes() {
        final List<Chip> ChipesContainer = new ArrayList<>();
        Cecole = findViewById(R.id.check_ecole);
        Cmagasin = findViewById(R.id.check_magasin);
        CParc = findViewById(R.id.check_parc);
        Cbus = findViewById(R.id.check_bus);
        Cmetro = findViewById(R.id.check_metro);
        addingContainer(ChipesContainer);
        return ChipesContainer;
    }

    private void addingContainer(List<Chip> chipesContainer) {
        chipesContainer.add(Cecole);
        chipesContainer.add(Cmagasin);
        chipesContainer.add(CParc);
        chipesContainer.add(Cbus);
        chipesContainer.add(Cmetro);
    }

    private List<String> activateChip(final List<Chip> ChipesContainer) {
        final List<String> resultList = new ArrayList<>();
        for (int i = 0; i < ChipesContainer.size(); i++) {
            final int finalI = i;
            ChipesContainer.get(i).setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    if (ChipesContainer.get(finalI).isChecked()) {
                        globalResultEstate.put(ChipesContainer.get(finalI).getText().toString(), String.valueOf(ChipesContainer.get(finalI).getText()));
                        resultList.add(String.valueOf(ChipesContainer.get(finalI).getText()));
                    } else if (!ChipesContainer.get(finalI).isChecked()) {
                        globalResultEstate.remove(ChipesContainer.get(finalI).getText().toString(), String.valueOf(ChipesContainer.get(finalI).getText()));
                        resultList.remove(String.valueOf(ChipesContainer.get(finalI).getText()));
                    }
                }
            });
        }
        return resultList;
    }

    private void deployeButton() {
        initiateAndActivateDateButton();
        initiateAndActivateDateSellButton();
        initiateAndActivateLocalButton();
        initiateAndActivateCameraButton();
        initiateAndActivateCancelButton();
        initiateAndActivateOkButton();
        initiateAndActivateModifyButton();
    }

    private void initiateAndActivateDateButton() {
        Button btnDate = findViewById(R.id.btn_date);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "Date Picker2");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        String date = Utils.getDateFormat(this, c);
        FragmentManager fragmanager = getSupportFragmentManager();
        if (fragmanager.findFragmentByTag("Date Picker1") != null) {
            edit_ontheSell.setText(date);
        }
        if (fragmanager.findFragmentByTag("Date Picker2") != null) {
            eMarket.setText(date);
        }
    }

    private void initiateAndActivateDateSellButton() {
        Button btnDateSell = findViewById(R.id.btn_date_Sell);
        btnDateSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "Date Picker1");
            }
        });
    }

    private void redirectToDetailsActivity(RealEstate estateForModifier) {
        finish();
    }

    private boolean checkIfEveryFieldIsHere() {
        boolean isCheck = false;
        List<TextInputLayout> entryUser = new ArrayList<>();
        entryUser.add(eAdress);
        entryUser.add(eChambre);
        entryUser.add(ePiece);
        entryUser.add(ePostal);
        entryUser.add(ePrix);
        entryUser.add(eSdb);
        entryUser.add(eSurface);
        entryUser.add(eVille);
        for (int i = 0; i < entryUser.size(); i++) {
            if (entryUser.get(i).getEditText().getText().toString().isEmpty()) {
                entryUser.get(i).setError("*");
                isCheck = true;
            }
            if (isCheck) {
                Toast.makeText(this, R.string.vous_devez, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void initiateAndActivateCameraButton() {
        Button btnLocalPhoto = findViewById(R.id.btn_photo_on_phone);
        btnLocalPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraActivate = false;
                askDescription();
            }
        });
    }

    private void initiateAndActivateLocalButton() {
        Button btnCameraPhoto = findViewById(R.id.btn_local_photo);
        btnCameraPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraActivate = true;
                askDescription();
            }
        });
    }

    private void askDescription() {
        AlertDialog alertDialog = DescriptionAlertDIalg();
        alertDialog.show();
    }

    private AlertDialog DescriptionAlertDIalg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.input_description, null);
        builder.setView(view).setTitle(R.string.define_ins).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TextInputLayout editText = view.findViewById(R.id.inputDescripptionEdittext);
                descritpionImage.add(editText.getEditText().getText().toString());
                knowWhere2Go();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }

    private void knowWhere2Go() {
        if (CameraActivate) {
            goToGalleryPhoto();
        } else {
            goToCameraDisplay();
        }
    }

    private void goToGalleryPhoto() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUESTCODEGALLERY);
    }

    private void goToCameraDisplay() {

        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, IMAGE_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                String imageurl = getRealPathFromURI(imageUri);
                listPhotoRealistetate.add(imageUri.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if ((requestCode == REQUESTCODEGALLERY) && (resultCode == RESULT_OK)) {
            Uri selectedImage = data.getData();
            listPhotoRealistetate.add(selectedImage.toString());
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void initiateAndActivateCancelButton() {
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initiateAndActivateOkButton() {
        btnOk = findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkIfEveryFieldIsHere()) {
                    getTimeIfDateIsEmpty(eMarket);
                    saveEntryEditText();
                    showGlobalResult();
                }
            }
        });
    }

    private void getTimeIfDateIsEmpty(TextView bloc) {
        if (bloc.getText().toString().trim().isEmpty() || bloc.getText().toString().contains("date")) {
            String date = Utils.getDateFormat(this, Calendar.getInstance());
            bloc.setText(date);
        }
    }

    private void showGlobalResult() {
        Utils.buttonBlocker(btnOk);
        Utils.buttonBlocker(btnCancel);
        RealEstate estate1 = generateEstateObject();
        Utils.notifyme(this);
        if (estate1 != null) {
            if (listPhotoRealistetate.size() > 0) {
                sendPhotoAtBDD(estate1, estate1);
            } else {
                knowIfTempOrNot(estate1);
                finish();
            }
        }
    }

    private void sendPhotoAtBDD(RealEstate estate1, final RealEstate finalEstate) {
        if (Utils.internetOnVerify(this)) {
            sendToFireStock(estate1, new SendCallBack() {
                @Override
                public void onFinish(RealEstate estateFireBase) {
                    knowIfTempOrNot(estateFireBase);
                    insertToRoom(estateFireBase);
                    finish();
                }

                @Override
                public void onFail() {
                    knowIfTempOrNot(finalEstate);
                    insertToRoom(finalEstate);
                    finish();
                }
            });
        } else {
            knowIfTempOrNot(finalEstate);
        }
    }

    private void insertToRoom(RealEstate estate1) {
        dataBaseSQL.estateDao().insertEstate(estate1);
        insertCHildToRoom(estate1);

    }

    private void insertCHildToRoom(RealEstate estate1) {
        for (int i = 0; i < listPhotoRealistetate.size(); i++) {
            ImagesRealEstate imagesRealEstate = new ImagesRealEstate(estate1.getId(), globalResultEstate.get("Description"), listPhotoRealistetate.get(i), "notlinked");
            imagesRealEstate.setId(imagesRealEstate.hashCode());
            dataBaseSQL.imageDao().insertEstate(imagesRealEstate);
        }
        nearbyinsertinSQLit(estate1);
    }

    private void nearbyinsertinSQLit(RealEstate estate1) {
        for (int i = 0; i < resultsValidatedByUser.size(); i++) {
            NearbyEstate imagesRealEstate = new NearbyEstate(estate1.getId(), resultsValidatedByUser.get(i));
            imagesRealEstate.setId(imagesRealEstate.hashCode());
            dataBaseSQL.nearbyDao().insertNearby(imagesRealEstate);
        }
    }

    private RealEstate generateEstateObject() {
        String selledEstated;
        if (isItChecked) {
            selledEstated = Utils.reformatDate(globalResultEstate.get("dateSell"));
        } else {
            selledEstated = globalResultEstate.get("dateSell");
        }
        estate = new RealEstate(String.valueOf(true), String.valueOf(isItChecked), globalResultEstate.get("TypeEstate"), globalResultEstate.get("nameEstate"), globalResultEstate.get("Adresse"),
                Integer.valueOf(globalResultEstate.get("Chambre")), globalResultEstate.get("Description"), Utils.reformatDate(globalResultEstate.get("date")), Integer.valueOf(globalResultEstate.get("Postal")), Integer.valueOf(globalResultEstate.get("Piece"))
                , Integer.valueOf(globalResultEstate.get("Prix")), Integer.valueOf(globalResultEstate.get("SDB")), Integer.valueOf(globalResultEstate.get("Surface")), globalResultEstate.get("Ville"), selledEstated, lattitudeRealEState, longitudeRealEState, url);
        estate.setId(estate.hashCode());
        return estate;
    }

    private void sendToFireStock(final RealEstate estate1, final SendCallBack sendCallBack) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            List<String> url = Utils.uploadImage(estate1, listPhotoRealistetate, this, new Utils.CallBackImage() {
                @Override
                public void onFinish(List<String> s) {
                    link.addAll(s);
                    progressBar.setVisibility(View.INVISIBLE);
                    sendCallBack.onFinish(estate1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.INVISIBLE);
            sendCallBack.onFail();
        }
    }

    private void knowIfTempOrNot(RealEstate estate) {
        if (Utils.internetOnVerify(this)) {
            Utils.sendItToMyBDDatRealEstate(estate);
            for (int i = 0; i < listPhotoRealistetate.size(); i++) {
                ImagesRealEstate imagesRealEstate = new ImagesRealEstate(estate.getId(), globalResultEstate.get("Description"), listPhotoRealistetate.get(i), link.get(i));
                imagesRealEstate.setId(imagesRealEstate.hashCode());
                Utils.sendMyBDDImagePlease(imagesRealEstate);
            }
            for (int i = 0; i < resultsValidatedByUser.size(); i++) {
                NearbyEstate nearbyEstate = new NearbyEstate(estate.getId(), resultsValidatedByUser.get(i));
                nearbyEstate.setId(nearbyEstate.hashCode());
                Utils.sendMyBDDNearbyPlease(nearbyEstate);
            }
            Toast.makeText(AddInformationActivity.this, R.string.filesuploads, Toast.LENGTH_SHORT).show();
            estate.setTempInsert("false");
        } else {
            estate = saveInTempIfNoInternet(estate);
            insertToRoom(estate);
            finish();
        }
    }

    private RealEstate saveInTempIfNoInternet(RealEstate estate) {
        if (!Utils.internetOnVerify(this)) {
            Toast.makeText(this, R.string.not_connecte, Toast.LENGTH_LONG).show();
            tempListInsert.add(estate);
            progressBar.setVisibility(View.GONE);
            estate.setTempInsert("true");
        }
        return estate;
    }


    private void deployModificationAction() {
        Boolean isEstateExist = verifyEstateExistAlreadyForModify();
        if (isEstateExist) {
            giveEstat2ViewsIfNotNull();
            replaceOkButtonByModifyButton();
        }
    }

    private void giveEstat2ViewsIfNotNull() {
        editTextCase();
        spinnerCaseType();
        spinnerCaseAgent();
        ChipCase();
        photoCAse();
        switchCase();
    }

    private void photoCAse() {
        if (dataBaseSQL.imageDao().selectAllImageDeuxFois(estate.getId()) != null) {
            dataBaseSQL.imageDao().selectAllImageDeuxFois(estate.getId()).observe(this, new Observer<List<ImagesRealEstate>>() {
                @Override
                public void onChanged(List<ImagesRealEstate> imagesRealEstateList) {
                    listPhotoRealistetate.clear();
                    for (int i = 0; i < imagesRealEstateList.size(); i++) {
                        listPhotoRealistetate.add(imagesRealEstateList.get(i).getImage());
                        descritpionImage.add(imagesRealEstateList.get(i).getDescriptionImage());
                    }
                }
            });
        }
    }

    private void switchCase() {
        if (Boolean.valueOf(estate.getIschecked()) || !estate.getSelled().equals("date")) {
            switchVendu.setChecked(true);
            selled = true;

        } else {
            switchVendu.setChecked(false);
            selled = false;

        }
    }

    private void ChipCase() {
        final List<Chip> checkForPosition = initiateChipes();
        if (dataBaseSQL.nearbyDao().selectAllNearbyCondition(estate.getId()) != null) {
            dataBaseSQL.nearbyDao().selectAllNearbyCondition(estate.getId()).observe(this, new Observer<List<NearbyEstate>>() {
                @Override
                public void onChanged(List<NearbyEstate> nearbyEstates) {
                    List<String> listNearby = new ArrayList<>();
                    for (int i = 0; i < nearbyEstates.size(); i++) {
                        listNearby.add(nearbyEstates.get(i).getNearby());
                    }
                    for (int i = 0; i < checkForPosition.size(); i++) {
                        if (listNearby.contains(checkForPosition.get(i).getText().toString())) {
                            checkForPosition.get(i).setChecked(true);
                        }
                    }

                }
            });
        }
    }

    private void spinnerCaseType() {
        String[] spinnerCAseType;
        spinnerCAseType = getResources().getStringArray(R.array.Spinner_items);
        for (int i = 0; i < spinnerCAseType.length; i++) {
            if (estate.getType().equals(spinnerCAseType[i])) {
                spinnerChoicce.setSelection(i);
            }
        }
    }

    private void spinnerCaseAgent() {
        String[] spinnerCAseType;
        spinnerCAseType = getResources().getStringArray(R.array.Spinner_agent);
        for (int i = 0; i < spinnerCAseType.length; i++) {
            if (estate.getNomAgent().equals(spinnerCAseType[i])) {
                spinnerNom.setSelection(i);
            }
        }
    }

    private void editTextCase() {
        eAdress.getEditText().setText(estate.getAdresse());
        eChambre.getEditText().setText(String.valueOf(estate.getChambre()));
        eDescr.getEditText().setText(estate.getDescription());
        eMarket.setText(Utils.reformatInverseDate(estate.getMarket()));
        edit_ontheSell.setText(estate.getSelled());
        ePiece.getEditText().setText(String.valueOf(estate.getPiece()));
        ePostal.getEditText().setText(String.valueOf(estate.getPostal()));
        ePrix.getEditText().setText(String.valueOf(estate.getPrix()));
        eSdb.getEditText().setText(String.valueOf(estate.getSdb()));
        eSurface.getEditText().setText(String.valueOf(estate.getSurface()));
        eVille.getEditText().setText(estate.getTown());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<String> activateChipCase(final List<Chip> ChipesContainer) {
        final List<String> resultList = new ArrayList<>();
        for (int i = 0; i < ChipesContainer.size(); i++) {
            if (ChipesContainer.get(i).isChecked()) {
                globalResultEstate.put(ChipesContainer.get(i).getText().toString(), String.valueOf(ChipesContainer.get(i).getText()));
                resultList.add(String.valueOf(ChipesContainer.get(i).getText()));
            } else if (!ChipesContainer.get(i).isChecked()) {
                globalResultEstate.remove(ChipesContainer.get(i).getText().toString(), String.valueOf(ChipesContainer.get(i).getText()));
                resultList.remove(String.valueOf(ChipesContainer.get(i).getText()));
            }
        }
        return resultList;
    }

    private void replaceOkButtonByModifyButton() {
        btnOk.setVisibility(View.GONE);
        btnModify.setVisibility(View.VISIBLE);
    }

    private void initiateAndActivateModifyButton() {
        btnModify = findViewById(R.id.btn_Modifier);
        btnModify.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Utils.buttonBlocker(btnModify);
                Utils.buttonBlocker(btnCancel);
                RealEstate estateForModifier = modifyEstate();
                if (Utils.internetOnVerify(AddInformationActivity.this)) {
                    Utils.upDateMyBDDPlease(modifyEstate(), estate);
                    handleImageUpdate();
                    handleNearbyUpdate();

                    if (listPhotoRealistetate.size() > 0) {
                        try {
                            progressBar.setVisibility(View.VISIBLE);
                            Utils.uploadImage(modifyEstate(), listPhotoRealistetate, AddInformationActivity.this, new Utils.CallBackImage() {
                                @Override
                                public void onFinish(List<String> s) {
                                    progressBar.setVisibility(View.GONE);
                                    link.addAll(s);
                                    finish();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        redirectToDetailsActivity(modifyEstate());
                    }

                } else {
                    saveInTempUpdateIfNoInternet(estateForModifier);
                    updateSQLite(estateForModifier);
                    redirectToDetailsActivity(modifyEstate());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleImageUpdate() {
        Utils.eraseDataImageInBDD(modifyEstate().getId(), new Utils.CallbackErase() {
            @Override
            public void onFinish() {
                for (int i = 0; i < listPhotoRealistetate.size(); i++) {
                    ImagesRealEstate imagesRealEstate = new ImagesRealEstate(estate.getId(), globalResultEstate.get("Description"), listPhotoRealistetate.get(i), "notlinked");
                    imagesRealEstate.setId(imagesRealEstate.hashCode());
                    Utils.upDateMyBDDImagePlease(imagesRealEstate);
                }
            }
        });

    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleNearbyUpdate() {
        Utils.eraseDataNEarbyInBDD((modifyEstate().getId()), new Utils.CallbackErase() {
            @Override
            public void onFinish() {
                for (int i = 0; i < resultsValidatedByUser.size(); i++) {
                    NearbyEstate nearbyEstate = new NearbyEstate(estate.getId(), resultsValidatedByUser.get(i));
                    nearbyEstate.setId(nearbyEstate.hashCode());
                    Utils.upDateMyBDDNearbyPlease(nearbyEstate);
                }
            }
        });
    }

    private void saveInTempUpdateIfNoInternet(RealEstate estate) {
        if (!Utils.internetOnVerify(this)) {
            Toast.makeText(this, R.string.not_connecte, Toast.LENGTH_LONG).show();
            tempListUpdate.add(estate);
            estate.setTempUpdate("true");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private RealEstate modifyEstate() {
        saveEntryEditText();
        resultsValidatedByUser = activateChipCase(ChipesContainer);
        String selledEstated;
        if (isItChecked) {
            selledEstated = Utils.reformatDate(globalResultEstate.get("dateSell"));
        } else {
            selledEstated = globalResultEstate.get("dateSell");
        }
        RealEstate estateNew = new RealEstate(String.valueOf(true), String.valueOf(selled), globalResultEstate.get("TypeEstate"), globalResultEstate.get("nameEstate"), globalResultEstate.get("Adresse"),
                Integer.valueOf(globalResultEstate.get("Chambre")), globalResultEstate.get("Description"), Utils.reformatDate(globalResultEstate.get("date")), Integer.valueOf(globalResultEstate.get("Postal")), Integer.valueOf(globalResultEstate.get("Piece"))
                , Integer.valueOf(globalResultEstate.get("Prix")), Integer.valueOf(globalResultEstate.get("SDB")), Integer.valueOf(globalResultEstate.get("Surface")), globalResultEstate.get("Ville"), selledEstated, lattitudeRealEState, longitudeRealEState, url);
        estateNew.setId(estate.getId());
        updateSQLite(estateNew);
        return estateNew;
    }

    private void updateSQLite(RealEstate estateNew) {
        dataBaseSQL = DataBaseSQL.getInstance(AddInformationActivity.this);
        dataBaseSQL.estateDao().upDateEstate(estateNew);
        dataBaseSQL.imageDao().DeletePArticularEstatesGroup(estateNew.getId());
        imageinsertInSqlite(estateNew);
        dataBaseSQL = DataBaseSQL.getInstance(AddInformationActivity.this);
        dataBaseSQL.nearbyDao().DeletePArticularNearbysGroup(estateNew.getId());
        nearbyinsertinSQLit(estate);
    }

    private void imageinsertInSqlite(RealEstate estateNew) {
        for (int i = 0; i < listPhotoRealistetate.size(); i++) {
            ImagesRealEstate imagesRealEstate = new ImagesRealEstate(estateNew.getId(), globalResultEstate.get("Description"), listPhotoRealistetate.get(i), "notLinked");
            imagesRealEstate.setId(imagesRealEstate.hashCode());
            dataBaseSQL.imageDao().insertEstate(imagesRealEstate);
        }
    }

    private void deployRecyclerView() {
        AdaptateurImage adapter = new AdaptateurImage(listPhotoRealistetate, this, descritpionImage, "true");
        RecyclerView recyclerView = findViewById(R.id.Recyclerviewphotos);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddInformationActivity.this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        deployRecyclerView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        deployRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deployRecyclerView();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Adresse", eAdress.getEditText().getText().toString());
        outState.putString("Chambre", eChambre.getEditText().getText().toString());
        outState.putString("Descr", eDescr.getEditText().getText().toString());
        outState.putString("Date", eMarket.getText().toString());
        outState.putString("onsell", edit_ontheSell.getText().toString());
        outState.putString("piece", ePiece.getEditText().getText().toString());
        outState.putString("postal", ePostal.getEditText().getText().toString());
        outState.putString("prix", ePrix.getEditText().getText().toString());
        outState.putString("sdb", eSdb.getEditText().getText().toString());
        outState.putString("surface", eSurface.getEditText().getText().toString());
        outState.putString("ville", eVille.getEditText().getText().toString());
        outState.putString("type", spinnerChoicce.getSelectedItem().toString());
        outState.putString("nom_agent", spinnerNom.getSelectedItem().toString());
        outState.putStringArrayList("photo", (ArrayList<String>) listPhotoRealistetate);
        outState.putStringArrayList("description_image", (ArrayList<String>) descritpionImage);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        eAdress.getEditText().setText(savedInstanceState.getString("Adresse"));
        eChambre.getEditText().setText(savedInstanceState.getString("Chambre"));
        eDescr.getEditText().setText(savedInstanceState.getString("Descr"));
        eMarket.setText(savedInstanceState.getString("Date"));
        edit_ontheSell.setText(savedInstanceState.getString("onsell"));
        ePiece.getEditText().setText(savedInstanceState.getString("piece"));
        ePostal.getEditText().setText(savedInstanceState.getString("postal"));
        ePrix.getEditText().setText(savedInstanceState.getString("prix"));
        eSdb.getEditText().setText(savedInstanceState.getString("sdb"));
        eSurface.getEditText().setText(savedInstanceState.getString("surface"));
        eVille.getEditText().setText(savedInstanceState.getString("ville"));
        listPhotoRealistetate.clear();
        listPhotoRealistetate.addAll(savedInstanceState.getStringArrayList("photo"));
        descritpionImage.clear();
        descritpionImage.addAll(savedInstanceState.getStringArrayList("description_image"));
        getSpinnerentryType(savedInstanceState);
        getSpinnerentryAgent(savedInstanceState);

    }

    public void getSpinnerentryType(Bundle savedInstanceState) {
        String[] spinnerentry;
        spinnerentry = getResources().getStringArray(R.array.Spinner_agent);
        for (int i = 0; i < spinnerentry.length; i++) {
            if (savedInstanceState.getString("type").equals(spinnerentry[i])) {
                spinnerChoicce.setSelection(i);
            }
        }
    }

    public void getSpinnerentryAgent(Bundle savedInstanceState) {
        String[] spinnerentryAgent;
        spinnerentryAgent = getResources().getStringArray(R.array.Spinner_agent);
        for (int i = 0; i < spinnerentryAgent.length; i++) {
            if (savedInstanceState.getString("nom_agent").equals(spinnerentryAgent[i])) {
                spinnerChoicce.setSelection(i);
            }
        }
    }

    public interface SendCallBack {
        void onFinish(RealEstate estateFireBase);

        void onFail();
    }
}
