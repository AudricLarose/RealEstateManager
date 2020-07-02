package com.openclassrooms.realestatemanager.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.openclassrooms.realestatemanager.utils.Adaptateur;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.utils.DatePickerFragment;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "ShowResult";
    private static final int IMAGE_REQUEST = 102;
    private static final int REQUESTCODEGALLERY = 101;
    private static boolean isItChecked = false;
    private static RealEstate estate;
    private static String dateActuelle, date;
    List<TextInputLayout> editTextContainer = new ArrayList<>();
    private Chip Cecole, Cmagasin, Cmetro, CParc, Cbus, cNone, cAp, cAttic, cLoft, cHouse;
    private TextInputLayout ePrixmin, eSurfaceMax, ePrixMax, eSurface, ePiece, eChambre, eSdb, eTown;
    private Switch search_switch_vendu;
    private TextView eMarket;
    private Spinner spinerPhoto, spinnerAgent, spinnersell;
    private Switch switchVendu;
    private Button btnOk, btnReset, btnDate, btnDateSell;
    private List<String> resultsValidatedByUserForNearBy = new ArrayList<>();
    private List<String> globalResult = new ArrayList<>();
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private Map<String, String> globalResultEstate = new HashMap<>();
    private RelativeLayout relativeLayoutSell;
    private TextView edit_ontheSell;
    private Adaptateur adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private List<String> globaleResulforCompare = new ArrayList<>();
    private static List<RealEstate> resultResearchRealEstate = new ArrayList<>();
    private List<String> resultsValidatedByUserForPhotos = new ArrayList<>();
    private String resultsValidatedByUserForAgent;
    private String resultsValidatedByUserForSell;
    private List<String> resultsValidatedByUserForTypes;
    private int positionSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.Search);
        resultResearchRealEstate.addAll(listRealEstate);
        actionfleche();
        deployRecyclerView();
        iniatiateAndActivateSwitch();
        editTextContainer = initiateEditText();
        deployeChipes();
        deployButton();
        deploySwitch();
        deployRecyclerView();
        deployRelativeLayout();
        deployeSpinner();
    }


    private void actionfleche() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void deployRecyclerView() {
        adapter = new Adaptateur(resultResearchRealEstate, false, null);
        recyclerView = findViewById(R.id.search_RecyclerSearch);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void deployRelativeLayout() {
        relativeLayoutSell = findViewById(R.id.search_RelativeSellDetails);
    }

    private void deploySwitch() {
        search_switch_vendu = findViewById(R.id.search_switch_vendu);
    }

    private List<TextInputLayout> initiateEditText() {
        List<TextInputLayout> editTextsContainer = new ArrayList<>();
        eChambre = findViewById(R.id.ChamberMin);
        eMarket = findViewById(R.id.search_edit_onthemarket);
        ePiece = findViewById(R.id.piecemin);
        ePrixmin = findViewById(R.id.minprice);
        ePrixMax = findViewById(R.id.prixmax);
        eSdb = findViewById(R.id.sdbmin);
        eSurface = findViewById(R.id.surfacemin);
        eSurfaceMax = findViewById(R.id.surfacemax);
        edit_ontheSell = findViewById(R.id.search_edit_ontheSell);
        eTown = findViewById(R.id.townSearch);
        editTextsContainer.add(eChambre);
        editTextsContainer.add(ePrixmin);
        editTextsContainer.add(ePiece);
        editTextsContainer.add(ePrixMax);
        editTextsContainer.add(eSdb);
        editTextsContainer.add(eSurface);
        editTextsContainer.add(eSurfaceMax);
        return editTextsContainer;
    }

    private void saveEntryEditText() {
        globalResultEstate.put("PrixMin", ePrixmin.getEditText().getText().toString());
        globalResultEstate.put("Chambre", eChambre.getEditText().getText().toString());
        globalResultEstate.put("Piece", ePiece.getEditText().getText().toString());
        globalResultEstate.put("PrixMax", ePrixMax.getEditText().getText().toString());
        globalResultEstate.put("SDB", eSdb.getEditText().getText().toString());
        globalResultEstate.put("SurfaceMin", eSurface.getEditText().getText().toString());
        globalResultEstate.put("SurfaceMAx", eSurfaceMax.getEditText().getText().toString());
        globalResultEstate.put("town", eTown.getEditText().getText().toString());
    }

    private void deployButton() {
        initiateAndActivateDateButton();
        initiateAndActivateOkButton();
        initiateAndActivateDateSellButton();
        initiateAndActivateResetButton();
    }

    private void initiateAndActivateResetButton() {
        btnReset = findViewById(R.id.search_btn_Reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        resetEntry();


    }

    private void resetEntry() {
        eMarket.setText("");
        edit_ontheSell.setText("");
    }

    private void reset() {
        resultResearchRealEstate.clear();
        resultResearchRealEstate.addAll(listRealEstate);
        deployRecyclerView();
    }

    private void initiateAndActivateOkButton() {
        btnOk = findViewById(R.id.search_btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                saveEntryEditText();
                addGlobalResult();
                compareListAll();

            }
        });
    }

    private void iniatiateAndActivateSwitch() {
        switchVendu = findViewById(R.id.search_switch_vendu);
        switchVendu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    relativeLayoutSell.setVisibility(View.VISIBLE);
                } else {
                    relativeLayoutSell.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initiateAndActivateDateButton() {
        btnDate = findViewById(R.id.search_btn_date);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "Date Picker2");
            }
        });
    }

    private void initiateAndActivateDateSellButton() {
        btnDateSell = findViewById(R.id.search_btn_date_Sell);
        btnDateSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "Date Picker1");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        dateActuelle = DateFormat.getDateInstance().format(c.getTime());
        date = Utils.getDateFormat(SearchActivity.this, c);
        FragmentManager fragmanager = getSupportFragmentManager();
        if (fragmanager.findFragmentByTag("Date Picker1") != null) {
            edit_ontheSell.setText(date);
        }
        if (fragmanager.findFragmentByTag("Date Picker2") != null) {
            eMarket.setText(date);
        }
    }


    private void deployeChipes() {
        final List<Chip> ChipesContainer = initiateChipesNearby();
        final List<Chip> ChipesContainer2 = initiateChipesType();
        resultsValidatedByUserForNearBy = activateChip(ChipesContainer);
        resultsValidatedByUserForTypes = activateChip(ChipesContainer2);
    }


    private void deployeSpinner() {
        initiateSpinner();
        putDataOnSpinnerSell();
        spinnerPhotoActivate();
        spinnerAgentActivate();
        spinnerSellActivate();
    }

    private void spinnerSellActivate() {
        spinnersell.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                resultsValidatedByUserForSell = adapterView.getSelectedItem().toString();
                switch (i) {
                    case 0:
                        positionSwitch = 0;
                        relativeLayoutSell.setVisibility(View.INVISIBLE);
                        break;

                    case 1:
                        positionSwitch = 1;
                        relativeLayoutSell.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        positionSwitch = 2;
                        relativeLayoutSell.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void spinnerAgentActivate() {
        spinnerAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                resultsValidatedByUserForAgent = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void spinnerPhotoActivate() {
        spinerPhoto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                resultsValidatedByUserForPhotos.add(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void putDataOnSpinnerSell() {
        List<String> categories = new ArrayList<String>();
        categories.add(getString(R.string.both));
        categories.add(getString(R.string.Available));
        categories.add(getString(R.string.selledstate));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersell.setAdapter(dataAdapter);
    }

    private void initiateSpinner() {
        spinerPhoto = findViewById(R.id.spinnerphotonbr);
        spinnerAgent = findViewById(R.id.spinnerAgent);
        spinnersell = findViewById(R.id.sellornot);
    }

    private List<Chip> initiateChipesNearby() {
        final List<Chip> ChipesContainer = new ArrayList<>();
        Cecole = findViewById(R.id.search_check_ecole);
        Cmagasin = findViewById(R.id.search_check_magasin);
        CParc = findViewById(R.id.search_check_parc);
        Cbus = findViewById(R.id.search_check_bus);
        Cmetro = findViewById(R.id.search_check_metro);
        cNone = findViewById(R.id.nonenearbysearch);
        cNoneIsCheckedEvent();
        ChipesContainer.add(Cecole);
        ChipesContainer.add(Cmagasin);
        ChipesContainer.add(CParc);
        ChipesContainer.add(Cbus);
        ChipesContainer.add(Cmetro);
        ChipesContainer.add(cNone);
        return ChipesContainer;
    }

    private void cNoneIsCheckedEvent() {
        if (cNone.isChecked()) {
            Cecole.setEnabled(false);
            Cmagasin.setEnabled(false);
            CParc.setEnabled(false);
            Cbus.setEnabled(false);
            Cmetro.setEnabled(false);
        } else if (!cNone.isChecked()) {
            Cecole.setEnabled(true);
            Cmagasin.setEnabled(true);
            CParc.setEnabled(true);
            Cbus.setEnabled(true);
            Cmetro.setEnabled(true);
        }
    }

    private List<Chip> initiateChipesType() {
        final List<Chip> ChipesContainer = new ArrayList<>();
        cAp = findViewById(R.id.search_check_appartement);
        cAttic = findViewById(R.id.search_check_attic);
        cHouse = findViewById(R.id.search_check_house);
        cLoft = findViewById(R.id.search_check_loft);
        ChipesContainer.add(cAp);
        ChipesContainer.add(cAttic);
        ChipesContainer.add(cHouse);
        ChipesContainer.add(cLoft);
        return ChipesContainer;
    }

    private List<String> activateChip(final List<Chip> ChipesContainer) {
        final List<String> resultList = new ArrayList<>();
        for (int i = 0; i < ChipesContainer.size(); i++) {
            final int finalI = i;
            ChipesContainer.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ChipesContainer.get(finalI).isChecked()) {
                        resultList.add(String.valueOf(ChipesContainer.get(finalI).getText()));
                    } else if (!ChipesContainer.get(finalI).isChecked()) {
                        resultList.remove(String.valueOf(ChipesContainer.get(finalI).getText()));
                    }
                }
            });
        }
        return resultList;
    }


    private void addGlobalResult() {
        globaleResulforCompare.addAll(globalResult);
        globaleResulforCompare.addAll(resultsValidatedByUserForNearBy);
    }


    private void compareListAll() {
        for (int i = 0; i < listRealEstate.size(); i++) {
            deleteSDBIfResultMatch(i);
            deletePieceIfResultMatch(i);
            deleteChambreIfResultMatch(i);
            deletePriceMinIfResultMatch(i);
            deletePriceMaxIfResultMatch(i);
            deleteSurfaceMinIfResultMatch(i);
            deleteSurfaceMaxIfResultMatch(i);
            deleteNumberPhotosByIfResultMatch(i);
            deleteNearByIfResultMatch(i);
            deleteDateEntryIfResultMatch(i);
            deleteDateSelledIfResultMatch(i);
            deleteTypeByIfResultMatch(i);
            deleteifsimplyselled(i);
            deleteifsimplyNotselled(i);
            deleteAgentByIfResultMatch(i);
            deleteTownIfResultMatch(i);
        }
        deployRecyclerView();
    }

    private void deleteDateEntryIfResultMatch(int i) {
        try {
            SimpleDateFormat sdf    = new SimpleDateFormat("dd-MM-yyyy");
            Date dateEntryByUSer = sdf.parse(eMarket.getText().toString().replace("/", "-"));
            Date dateRealEstate = sdf.parse(listRealEstate.get(i).getMarket().replace("/", "-"));
            if ((dateRealEstate.compareTo(dateEntryByUSer) < 0)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void deleteDateSelledIfResultMatch(int i) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date dateEntryByUSer = sdf.parse(edit_ontheSell.getText().toString().replace("/", "-"));
            Date dateRealEstate = sdf.parse(listRealEstate.get(i).getSelled().replace("/", "-"));
            if ((dateRealEstate.compareTo(dateEntryByUSer) < 0)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void deleteNearByIfResultMatch(int i) {

        if (resultsValidatedByUserForNearBy != null && resultsValidatedByUserForNearBy.size() != 0 && !listRealEstate.isEmpty()) {
            if (!listRealEstate.get(i).getNearby().containsAll(resultsValidatedByUserForNearBy)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
            if (cNone.isChecked()) {
                if (listRealEstate.get(i).getNearby().size() == 0) {
                    resultResearchRealEstate.remove(listRealEstate.get(i));
                }
            }
        }

    }

    private void deleteTypeByIfResultMatch(int i) {
        if (resultsValidatedByUserForTypes != null && resultsValidatedByUserForTypes.size() != 0 && !listRealEstate.isEmpty()) {
            if (!resultsValidatedByUserForTypes.contains(listRealEstate.get(i).getType())) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteNumberPhotosByIfResultMatch(int i) {
        for (int j = 0; j < resultsValidatedByUserForPhotos.size(); j++) {
            if (resultsValidatedByUserForPhotos.get(j) != null && !listRealEstate.isEmpty()) {
                if (listRealEstate.get(i).getPhotosReal().size() < Integer.valueOf(resultsValidatedByUserForPhotos.get(j))) {
                    resultResearchRealEstate.remove(listRealEstate.get(i));
                }
            }
        }
    }

    private void deleteAgentByIfResultMatch(int i) {
        if (resultsValidatedByUserForAgent != null && !listRealEstate.isEmpty()) {
            if (!listRealEstate.get(i).getNomAgent().contains(resultsValidatedByUserForAgent)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deletePriceMinIfResultMatch(int i) {
        String priceMaxEntryByUser = globalResultEstate.get("PrixMax");
        String priceOfMyList = listRealEstate.get(i).getPrix();
        if ((priceMaxEntryByUser != null && !priceMaxEntryByUser.isEmpty())) {
            if ((Integer.valueOf(priceMaxEntryByUser) < Integer.valueOf(priceOfMyList))) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deletePriceMaxIfResultMatch(int i) {

        String priceMinENtryByUser = globalResultEstate.get("PrixMin");
        String priceEstateList = listRealEstate.get(i).getPrix();
        if ((priceMinENtryByUser != null && !priceMinENtryByUser.isEmpty())) {
            if ((Integer.valueOf(priceMinENtryByUser) > Integer.valueOf(priceEstateList))) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteSurfaceMaxIfResultMatch(int i) {
        String surfaceMaxEntryByUSer = globalResultEstate.get("SurfaceMax");
        String surfaceEstateList = listRealEstate.get(i).getSurface();
        if ((surfaceMaxEntryByUSer != null && !surfaceMaxEntryByUSer.isEmpty())) {
            if ((Integer.valueOf(surfaceMaxEntryByUSer) < Integer.valueOf(surfaceEstateList))) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteSurfaceMinIfResultMatch(int i) {
        String surfaceMinEntryByUser = globalResultEstate.get("SurfaceMin");
        String surfaceEstateList = listRealEstate.get(i).getSurface();
        if ((surfaceMinEntryByUser != null && !surfaceMinEntryByUser.isEmpty())) {
            if ((Integer.valueOf(surfaceMinEntryByUser) > Integer.valueOf(surfaceEstateList))) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteChambreIfResultMatch(int i) {
        String chambreEntryByUser = globalResultEstate.get("Chambre");
        String chambreEstateList = listRealEstate.get(i).getChambre();
        if (chambreEntryByUser != null && !chambreEntryByUser.isEmpty()) {
            if ((Integer.valueOf(chambreEntryByUser) >= Integer.valueOf(chambreEstateList))) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deletePieceIfResultMatch(int i) {
        String pieceEntryByUser = globalResultEstate.get("Piece");
        String pieceEstateList = listRealEstate.get(i).getPiece();
        if (pieceEntryByUser != null && !pieceEntryByUser.isEmpty()) {
            if (Integer.valueOf(pieceEntryByUser) >= Integer.valueOf(pieceEstateList)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteSDBIfResultMatch(int i) {
        String sdbEntryByUser = globalResultEstate.get("SDB");
        String sdbEstateList = listRealEstate.get(i).getSdb();
        if (sdbEntryByUser != null && !sdbEntryByUser.isEmpty()) {
            if (Integer.valueOf(sdbEntryByUser) >= Integer.valueOf(sdbEstateList)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteTownIfResultMatch(int i) {
        String townEntryByUser = globalResultEstate.get("town").toLowerCase();
        String townEstateList = listRealEstate.get(i).getTown().toLowerCase();
        if (townEntryByUser != null && !townEntryByUser.isEmpty()) {
            if (!townEntryByUser.equals(townEstateList)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteifsimplyselled(int i) {
        String selledEstateList = listRealEstate.get(i).getSelled();
        if (positionSwitch == 2) {
            if ((selledEstateList.equals("")) || selledEstateList.equals("date")) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }

    }

    private void deleteifsimplyNotselled(int i) {
        String selledEstateList = listRealEstate.get(i).getSelled();
        if (positionSwitch == 1) {
            if (!selledEstateList.equals("date")) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Chambre", eChambre.getEditText().getText().toString());
        outState.putString("Date", eMarket.getText().toString());
        outState.putString("onsell", edit_ontheSell.getText().toString());
        outState.putString("piece", ePiece.getEditText().getText().toString());
        outState.putString("prixmax", ePrixMax.getEditText().getText().toString());
        outState.putString("prixmin", ePrixmin.getEditText().getText().toString());
        outState.putString("sdb", eSdb.getEditText().getText().toString());
        outState.putString("surfacemin", eSurface.getEditText().getText().toString());
        outState.putString("surfacemax", eSurfaceMax.getEditText().getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        eChambre.getEditText().setText(savedInstanceState.getString("Chambre"));
        eMarket.setText(savedInstanceState.getString("Date"));
        edit_ontheSell.setText(savedInstanceState.getString("onsell"));
        ePiece.getEditText().setText(savedInstanceState.getString("piece"));
        ePrixmin.getEditText().setText(savedInstanceState.getString("prixmin"));
        ePrixMax.getEditText().setText(savedInstanceState.getString("prixmax"));
        eSdb.getEditText().setText(savedInstanceState.getString("sdb"));
        eSurface.getEditText().setText(savedInstanceState.getString("surfacemin"));
        eSurfaceMax.getEditText().setText(savedInstanceState.getString("surfacemax"));

    }
}
