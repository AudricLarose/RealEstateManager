package com.openclassrooms.realestatemanager.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.openclassrooms.realestatemanager.Api.DataBaseSQL;
import com.openclassrooms.realestatemanager.dummy.ItemDetailActivity;
import com.openclassrooms.realestatemanager.dummy.MainActivity;
import com.openclassrooms.realestatemanager.utils.Adaptateur;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.utils.DatePickerFragment;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.modele.RealEstate;

import java.io.Serializable;
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
    private static String dateActuelle, date;
    List<TextInputLayout> editTextContainer = new ArrayList<>();
    private Chip Cecole, Cmagasin, Cmetro, CParc, Cbus, cNone, cAp, cAttic, cLoft, cHouse,cOthers;
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
    private String resultsValidatedByUserForPhotos;
    private String resultsValidatedByUserForAgent;
    private List<String> resultsValidatedByUserForTypes;
    private int positionSwitch;
    Integer priceMinENtryByUserValue = null;
    Integer priceMaxENtryByUserValue= null;
    Integer surfaceMaxENtryByUserValue= null;
    Integer surfaceMinENtryByUserValue= null;
    Integer chambreENtryByUserValue= null;
    Integer pieceENtryByUserValue= null;
    Integer SDBENtryByUserValue= null;
    String townENtryByUserValue= null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.Search);
        initialiseListRealEstate();
        actionfleche();
        iniatiateAndActivateSwitch();
        editTextContainer = initiateEditText();
        deployeChipes();
        deployButton();
        deploySwitch();
       // deployRecyclerView();
        deployRelativeLayout();
        deployeSpinner();
    }

    private void initialiseListRealEstate() {
        resultResearchRealEstate.clear();
        resultResearchRealEstate.addAll(listRealEstate);
    }


    private void actionfleche() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
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
        //deployRecyclerView();
    }

    private void initiateAndActivateOkButton() {
        btnOk = findViewById(R.id.search_btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                saveEntryEditText();
                addGlobalResult();
                //compareListAll();
                sendToSQLrequest();

            }
        });
    }

    private void sendToSQLrequest() {

        nulifyvalues();
        DataBaseSQL database = DataBaseSQL.getInstance(this);
        LiveData<List<RealEstate>> datalist = database.estateDao().selectAllEstateSorted(townENtryByUserValue,priceMinENtryByUserValue,priceMaxENtryByUserValue,surfaceMaxENtryByUserValue,surfaceMinENtryByUserValue,
                chambreENtryByUserValue,pieceENtryByUserValue,SDBENtryByUserValue,0,null,null);
        datalist.observe(this, new Observer<List<RealEstate>>() {
            @Override
            public void onChanged(List<RealEstate> realEstateList) {
                Toast.makeText(SearchActivity.this, "" + realEstateList.size(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void nulifyvalues() {

        String priceMinENtryByUser = globalResultEstate.get("PrixMin");
        String priceMaxENtryByUser = globalResultEstate.get("PrixMax");
        String surfaceMaxENtryByUser = globalResultEstate.get("SurfaceMax");
        String surfaceMinENtryByUser = globalResultEstate.get("SurfaceMin");
        String chambreENtryByUser = globalResultEstate.get("Chambre");
        String pieceENtryByUser = globalResultEstate.get("Piece");
        String SDBENtryByUser = globalResultEstate.get("SDB");
        String townENtryByUser = globalResultEstate.get("town");
        if (!priceMinENtryByUser.isEmpty()){
            priceMinENtryByUserValue = Integer.valueOf(priceMinENtryByUser);
        }

        if (!priceMaxENtryByUser.isEmpty()){
            priceMaxENtryByUserValue = Integer.valueOf(priceMaxENtryByUser);
        }
        if (!surfaceMaxENtryByUser.isEmpty()){
            surfaceMaxENtryByUserValue = Integer.valueOf(surfaceMaxENtryByUser);
        }

        if (!surfaceMinENtryByUser.isEmpty()){
            surfaceMinENtryByUserValue = Integer.valueOf(surfaceMinENtryByUser);
        }
        if (!chambreENtryByUser.isEmpty()){
            chambreENtryByUserValue = Integer.valueOf(chambreENtryByUser);
        }

        if (!pieceENtryByUser.isEmpty()){
            pieceENtryByUserValue = Integer.valueOf(pieceENtryByUser);
        }
        if (!SDBENtryByUser.isEmpty()){
            SDBENtryByUserValue = Integer.valueOf(SDBENtryByUser);
        }

        if (!townENtryByUser.isEmpty()){
            townENtryByUserValue = townENtryByUser;
        }
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
              adapterView.getSelectedItem();
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
                resultsValidatedByUserForPhotos=adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void putDataOnSpinnerSell() {
        String[] spinnerCAseType;
        spinnerCAseType=getResources().getStringArray(R.array.SellorNot);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerCAseType);
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
        cOthers = findViewById(R.id.search_other);
        ChipesContainer.add(cAp);
        ChipesContainer.add(cAttic);
        ChipesContainer.add(cHouse);
        ChipesContainer.add(cLoft);
        ChipesContainer.add(cOthers);
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
       //     deleteSDBIfResultMatch(i);
       //     deletePieceIfResultMatch(i);
       //     deleteChambreIfResultMatch(i);
       //     deletePriceMinIfResultMatch(i);
       //     deletePriceMaxIfResultMatch(i);
       //     deleteSurfaceMinIfResultMatch(i);
       //     deleteSurfaceMaxIfResultMatch(i);
      //      deleteNumberPhotosByIfResultMatch(i);
      //      deleteNearByIfResultMatch(i);
            deleteTypeByIfResultMatch(i);
            deleteDateEntryIfResultMatch(i);
            deleteDateSelledIfResultMatch(i);
            deleteifsimplyselled(i);
            deleteifsimplyNotselled(i);
       //     deleteAgentByIfResultMatch(i);
       //     deleteTownIfResultMatch(i);
        }

        //deployRecyclerView();
        goToNextResultActivity();
    }

    private void goToNextResultActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("RealEstate", (Serializable) resultResearchRealEstate);
        intent.putExtras(bundle);
        startActivity(intent);
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



    private void deleteTypeByIfResultMatch(int i) {
        if (resultsValidatedByUserForTypes != null && resultsValidatedByUserForTypes.size() != 0 && !listRealEstate.isEmpty()) {
            if (!resultsValidatedByUserForTypes.contains(listRealEstate.get(i).getType())) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
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
        int priceOfMyList = listRealEstate.get(i).getPrix();
        if ((priceMaxEntryByUser != null && !priceMaxEntryByUser.isEmpty())) {
            if ((Integer.valueOf(priceMaxEntryByUser) < priceOfMyList)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deletePriceMaxIfResultMatch(int i) {

        String priceMinENtryByUser = globalResultEstate.get("PrixMin");
        int priceEstateList = listRealEstate.get(i).getPrix();
        if ((priceMinENtryByUser != null && !priceMinENtryByUser.isEmpty())) {
            if ((Integer.valueOf(priceMinENtryByUser) > priceEstateList)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteSurfaceMaxIfResultMatch(int i) {
        String surfaceMaxEntryByUSer = globalResultEstate.get("SurfaceMax");
        int surfaceEstateList = listRealEstate.get(i).getSurface();
        if ((surfaceMaxEntryByUSer != null && !surfaceMaxEntryByUSer.isEmpty())) {
            if ((Integer.valueOf(surfaceMaxEntryByUSer) < surfaceEstateList)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteSurfaceMinIfResultMatch(int i) {
        String surfaceMinEntryByUser = globalResultEstate.get("SurfaceMin");
        int surfaceEstateList = listRealEstate.get(i).getSurface();
        if ((surfaceMinEntryByUser != null && !surfaceMinEntryByUser.isEmpty())) {
            if ((Integer.valueOf(surfaceMinEntryByUser) > surfaceEstateList)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteChambreIfResultMatch(int i) {
        String chambreEntryByUser = globalResultEstate.get("Chambre");
        int chambreEstateList = listRealEstate.get(i).getChambre();
        if (chambreEntryByUser != null && !chambreEntryByUser.isEmpty()) {
            if ((Integer.valueOf(chambreEntryByUser) >= chambreEstateList)) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deletePieceIfResultMatch(int i) {
        String pieceEntryByUser = globalResultEstate.get("Piece");
        int pieceEstateList = listRealEstate.get(i).getPiece();
        if (pieceEntryByUser != null && !pieceEntryByUser.isEmpty()) {
            if (Integer.valueOf(pieceEntryByUser) >= pieceEstateList) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteSDBIfResultMatch(int i) {
        String sdbEntryByUser = globalResultEstate.get("SDB");
        int sdbEstateList = listRealEstate.get(i).getSdb();
        if (sdbEntryByUser != null && !sdbEntryByUser.isEmpty()) {
            if (Integer.valueOf(sdbEntryByUser) >= sdbEstateList) {
                resultResearchRealEstate.remove(listRealEstate.get(i));
            }
        }
    }

    private void deleteTownIfResultMatch(int i) {
        String townEntryByUser = globalResultEstate.get("town").toLowerCase();
        String townEstateList = listRealEstate.get(i).getTown().toLowerCase();
        if (townEntryByUser != null && !townEntryByUser.isEmpty()) {
            if (!townEntryByUser.contains(townEstateList)) {
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
