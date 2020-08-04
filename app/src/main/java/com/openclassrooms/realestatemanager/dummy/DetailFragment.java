package com.openclassrooms.realestatemanager.dummy;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.realestatemanager.Api.DI;
import com.openclassrooms.realestatemanager.Api.DataBaseSQL;
import com.openclassrooms.realestatemanager.Api.ExtendedServiceEstate;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.activity.AddInformationActivity;
import com.openclassrooms.realestatemanager.activity.MainActivity;
import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;
import com.openclassrooms.realestatemanager.modele.NearbyEstate;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.openclassrooms.realestatemanager.utils.AdaptateurImage;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class DetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private static RealEstate estateGrabbed;
    private DummyContent.DummyItem mItem;
    private TextView adresse, ville, region, pays, surfaceChiffre, priceChiffre, roomChiffre, typeCHiffre, bathroomchiffre, nearbyChiffre, vendu, chamber, description, agent;
    private AdaptateurImage adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ExtendedServiceEstate serviceEstate = DI.getService();
    private List<RealEstate> listRealEstate = serviceEstate.getRealEstateList();
    private List<String> listDescription = new ArrayList<>();
    private List<String> listImage = new ArrayList<>();
    private boolean amIInEuro = true;
    private LatLng latLngRealestate;
    private Boolean modePhone;
    private RelativeLayout relativeLayout;
    private ImageView imageCarte;
    private DataBaseSQL dataBaseSQL = DataBaseSQL.getInstance(getContext());

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            String id = getArguments().getString(ARG_ITEM_ID);
            if (getArguments() != null && id != null) {
                modePhone = false;
                for (int i = 0; i < listRealEstate.size(); i++) {
                    if (String.valueOf(listRealEstate.get(i).getId()).contains(getArguments().getString(ARG_ITEM_ID))) {
                        estateGrabbed = listRealEstate.get(i);
                    }
                }
            } else {
                modePhone = true;

            }
            Activity activity = this.getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        deployTextView(rootView);
        realStateIfExist(rootView);
        return rootView;
    }


    private void deployCarteImageView(String url, View container) {
        imageCarte = container.findViewById(R.id.mapLiteView);
        Picasso.get().load(url).into(imageCarte);
    }

    private void takeImageAndDesciption(final View container) {
        DataBaseSQL database = DataBaseSQL.getInstance(getContext());
        LiveData<List<ImagesRealEstate>> datalist = database.imageDao().selectAllImageDeuxFois(estateGrabbed.getId());

        try {
            datalist.observe(getViewLifecycleOwner(), new Observer<List<ImagesRealEstate>>() {
                @Override
                public void onChanged(List<ImagesRealEstate> imagesRealEstates) {
                    for (int i = 0; i < imagesRealEstates.size(); i++) {
                        if (imagesRealEstates.get(i).getLinkFb() != null && !imagesRealEstates.get(i).getLinkFb().equals("") && (!imagesRealEstates.get(i).getLinkFb().equals("notLinked") && !imagesRealEstates.get(i).getLinkFb().equals("notlinked"))) {
                            listImage.add(imagesRealEstates.get(i).getLinkFb());
                        }else{
                            listImage.add(imagesRealEstates.get(i).getImage());
                        }
                    }
                }
            });
            datalist.observe(getViewLifecycleOwner(), new Observer<List<ImagesRealEstate>>() {
                @Override
                public void onChanged(List<ImagesRealEstate> imagesRealEstates) {
                    for (int i = 0; i < imagesRealEstates.size(); i++) {
                        listDescription.add(imagesRealEstates.get(i).getDescriptionImage());
                    }
                    deployRecyclerViewDetails(container);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deployRecyclerViewDetails(View container) {
        if (listImage.size() > 0 && listDescription.size() > 0) {
            adapter = new AdaptateurImage(listImage, getContext(), listDescription, "false");
        }
        recyclerView = container.findViewById(R.id.RecycleDetails);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void deployTextView(View container) {
        initiateTextView(container);
    }

    private void initiateTextView(View container) {
        adresse = container.findViewById(R.id.adresseDetails);
        description = container.findViewById(R.id.descirptionInputDetails);
        ville = container.findViewById(R.id.villeDetails);
        pays = container.findViewById(R.id.paysDetails);
        surfaceChiffre = container.findViewById(R.id.SurfacechiffresDetails);
        priceChiffre = container.findViewById(R.id.PriceChiffresDetails);
        roomChiffre = container.findViewById(R.id.RoomechiffresDetails);
        typeCHiffre = container.findViewById(R.id.TypeChiffresDetails);
        bathroomchiffre = container.findViewById(R.id.BathroomchiffresDetails);
        nearbyChiffre = container.findViewById(R.id.NearbyChiffresDetails);
        chamber = container.findViewById(R.id.RoomschiffresDetails);
        agent = container.findViewById(R.id.agentDetails);
    }


    private void realStateIfExist(View container) {
        if (modePhone) {
            estateGrabbed = grabEstatFromMainActivity();
        }
        if (estateGrabbed != null) {
            takeImageAndDesciption(container);
            shareInformationsDetails(container);
            if (Utils.internetOnVerify(getContext())) {
                try2FindAddress(container);
            } else {
                findTheDefaultAdress();
            }
            initiateSwitchSell(container);

        }
    }

    private RealEstate grabEstatFromMainActivity() {
        RealEstate estate = null;
        Intent intent = getActivity().getIntent();
        Bundle extra = intent.getExtras();
        if (extra != null) {
            estate = (RealEstate) extra.getSerializable("RealEstate");
        }
        return estate;
    }

    private void shareInformationsDetails(View container) {
        surfaceChiffre.setText(String.valueOf(estateGrabbed.getSurface()));
        roomChiffre.setText(String.valueOf(estateGrabbed.getPiece()));
        typeCHiffre.setText(estateGrabbed.getType());
        bathroomchiffre.setText(String.valueOf(estateGrabbed.getSdb()));
        if (dataBaseSQL.nearbyDao().selectAllNearbyCondition(estateGrabbed.getId()) != null) {
            dataBaseSQL.nearbyDao().selectAllNearbyCondition(estateGrabbed.getId()).observe(getViewLifecycleOwner(), new Observer<List<NearbyEstate>>() {
                @Override
                public void onChanged(List<NearbyEstate> nearbyEstateList) {
                    List<String> nearby = new ArrayList<>();
                    for (int i = 0; i < nearbyEstateList.size(); i++) {
                        nearby.add(nearbyEstateList.get(i).getNearby());
                    }
                    String nearbylistline = nearby.toString();
                    String nearbyNew = nearbylistline.replace("[", "");
                    String nearbyChiffreNew = nearbyNew.replace("]", "");
                    nearbyChiffre.setText(nearbyChiffreNew);
                }
            });

        }
        chamber.setText(String.valueOf(estateGrabbed.getChambre()));
        description.setText(estateGrabbed.getDescription());
        agent.setText(estateGrabbed.getNomAgent());
        try {
            priceChiffre.setText(Utils.getEuroFormat(Integer.valueOf(estateGrabbed.getPrix())));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void initiateSwitchSell(View rootView) {
        relativeLayout = rootView.findViewById(R.id.RelativeSelledDetails);
        if (estateGrabbed.getIschecked() != null && estateGrabbed.getSelled() != null) {
            if (estateGrabbed.getSelled().equals("date")) {
                relativeLayout.setVisibility(View.INVISIBLE);
            } else {
                relativeLayout.setVisibility(View.VISIBLE);
                appearDateSell(rootView);
            }
        }
    }

    private void appearDateSell(View rootView) {
        vendu = rootView.findViewById(R.id.dateSelledDetails);
        vendu.setText(Utils.reformatInverseDate(estateGrabbed.getSelled()));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.retour_arriere_modifier, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.convert:
                knowIfConvertToEuroOrDOllar();
                return true;
            case R.id.modify:
                modifyThisEstate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void try2FindAddress(final View container) {
        Utils.findAddress(getContext(), estateGrabbed.getAdresse(), new Utils.AdressGenerators() {
            @Override
            public void onSuccess(List<Address> addressList) {
                findTheRightAdress(container, addressList);
            }

            @Override
            public void onFail() {
                findTheDefaultAdress();
            }
        });
    }

    private void findTheDefaultAdress() {
        adresse.setText(estateGrabbed.getAdresse());
        ville.setText(estateGrabbed.getTown());
        pays.setText(" ");
        imageCarte.setVisibility(View.GONE);
    }

    private void findTheRightAdress(View container, List<Address> addressList) {
        adresse.setText(addressList.get(0).getThoroughfare());
        ville.setText(estateGrabbed.getTown());
        pays.setText(addressList.get(0).getCountryName());
        latLngRealestate = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
        String url = Utils.linkBuilder(latLngRealestate);
        deployCarteImageView(url, container);
    }


    private void modifyThisEstate() {
        RealEstate estate = grabEstatFromMainActivity();
        phoneVersionForModify(estate);
        tabletVersionForModify(estate);
    }

    private void phoneVersionForModify(RealEstate estate) {
        if (estate != null) {
            Intent intent = new Intent(getContext(), AddInformationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("RealEstate", estate);
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void tabletVersionForModify(RealEstate estate) {
        if (estateGrabbed != null && estate == null) {
            Intent intent = new Intent(getContext(), AddInformationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("RealEstate", estateGrabbed);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void knowIfConvertToEuroOrDOllar() {
        if (amIInEuro) {
            convertEuroEnDollar();
            amIInEuro = false;
        } else {
            convertDollarEnEuro();
            amIInEuro = true;
        }
    }

    private void convertDollarEnEuro() {
        int converted = estateGrabbed.getPrix();
        priceChiffre.setText(converted + " Euros");
    }

    private void convertEuroEnDollar() {
        int converted = Utils.convertEuroToDollar(Integer.valueOf(estateGrabbed.getPrix()));
        priceChiffre.setText(converted + " Dollars");
    }
}
