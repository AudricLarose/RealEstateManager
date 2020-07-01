package com.openclassrooms.realestatemanager.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.flags.Singletons;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.dummy.ItemDetailActivity;
import com.openclassrooms.realestatemanager.dummy.DetailFragment;
import com.openclassrooms.realestatemanager.dummy.MainActivity;
import com.openclassrooms.realestatemanager.modele.RealEstate;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.util.Half.NaN;


public class Adaptateur extends RecyclerView.Adapter<Adaptateur.LeHolder> {
    public List<RealEstate> liste;
    public Boolean mTwoPane;
    public MainActivity mParentActivity;
    public RealEstate estate;
    public Boolean selected;
    int selected_position = RecyclerView.NO_POSITION; // You have to set this globally in the Adapter class



    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private void goToItemDetailsActivity(View view, RealEstate estate1) {
        Intent intent = new Intent(view.getContext(), ItemDetailActivity.class);
        //                    intent.putExtra(DetailFragment.ARG_ITEM_ID, item.id);

        Bundle bundle = new Bundle();
        bundle.putSerializable("RealEstate", estate1);
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }


    public Adaptateur(List<RealEstate> liste, Boolean mTwoPane, MainActivity mParentActivity) {
        this.liste = liste;
        this.mTwoPane = mTwoPane;
        this.mParentActivity = mParentActivity;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);

        return new LeHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final LeHolder holder, final int position) {
        if (mTwoPane){
            selected_position=0;
        }
        holder.relativeLayout.setBackgroundColor(selected_position == position ? Color.MAGENTA : Color.TRANSPARENT);
        if (selected_position== position){
            holder.prix.setTextColor(Color.WHITE);
            holder.ville.setTextColor(Color.BLACK);
            holder.type.setTextColor(Color.BLACK);
        }


        estate = liste.get(position);
        holder.type.setText(estate.getType());
        if (Boolean.valueOf(estate.getInEuro())) {
            try {
                holder.prix.setText(Utils.getEuroFormat(Integer.parseInt(estate.getPrix())));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            holder.prix.setText(Utils.getDollarFormat(Integer.parseInt(estate.getPrix())));

        }
        holder.ville.setText(estate.getTown());

        imageHandling(holder);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) return;
                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position =holder.getAdapterPosition();
                notifyItemChanged(selected_position);
//                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                replaceIfTablet(v, position);
            }
        });

        checkIfitsSellOrNot(holder);
        verifyIfitisTemp(holder);
    }

    private void imageHandling(@NonNull LeHolder holder) {
        if (estate.getPhotosReal() != null && estate.getPhotosReal().size() > 0) {
            if (estate.getLink() != null && estate.getLink().size() > 0) {
                Picasso.get().load(estate.getLink().get(0)).into(holder.imageRealestate);
            } else {
                Picasso.get().load(Uri.parse(estate.getPhotosReal().get(0))).into(holder.imageRealestate);
            }
        }
    }

    private void replaceIfTablet(View v, int position) {
        if (mParentActivity != null) {
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putSerializable("RealEstate", liste.get(position));
                arguments.putString(DetailFragment.ARG_ITEM_ID, String.valueOf(liste.get(position).getId()));
                DetailFragment fragment = new DetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                goToItemDetailsActivity(v,liste.get(position));
            }
        }else {
            goToItemDetailsActivity(v,liste.get(position));
        }
    }

    @SuppressLint("ResourceAsColor")
    private void verifyIfitisTemp(LeHolder holder) {
        if (estate.getTemp()!=null && estate.getTemp().contains("true")) {
            holder.relativeLayout.setBackgroundColor(R.color.colorPrimary);
        }
   }

    private void checkIfitsSellOrNot(@NonNull LeHolder holder) {
        if (estate.getIschecked()!=null && estate.getSelled()!=null) {
            if (!Boolean.valueOf(estate.getIschecked()) && estate.getSelled().equals("date")) {
                holder.selled.setText(" Disponible !");
            } else {
                holder.selled.setText(" Vendu !");

            }
        }
    }

    @Override
    public int getItemCount() {
        if (liste != null) {
            return liste.size();
        } else {
            return 0;
        }
    }

    public static class LeHolder extends RecyclerView.ViewHolder {
        private TextView type, prix, ville, selled;
        private ImageView imageRealestate;
        private RelativeLayout relativeLayout;


        public LeHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.Row);
            imageRealestate = itemView.findViewById(R.id.imageRow);
            type = itemView.findViewById(R.id.TextTypeRow);
            prix = itemView.findViewById(R.id.TextPrixRow);
            ville = itemView.findViewById(R.id.TextTownRow);
            selled = itemView.findViewById(R.id.selledText);
        }
    }


}
