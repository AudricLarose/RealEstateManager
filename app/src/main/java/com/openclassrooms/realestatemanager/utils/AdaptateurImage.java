package com.openclassrooms.realestatemanager.utils;

import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.modele.ImagesRealEstate;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class AdaptateurImage extends RecyclerView.Adapter<AdaptateurImage.LeHolder> {
    static List<String> liste;
    Context context;
    public static List<String> listDescription= new ArrayList<>();
    public static List<ImagesRealEstate> list= new ArrayList<>();
    public String add;

    public AdaptateurImage(List<String> liste, Context context, List<String> listDescription,String add) {
        this.liste = liste;
        notifyDataSetChanged();
        this.context= context;
        this.listDescription= listDescription;
        this.add=add;
    }

    @NonNull
    @Override
    public LeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roow_image, parent, false);
        return new LeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LeHolder holder, final int position) {
        final String estateImage = liste.get(position);
        holder.progressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(Uri.parse(estateImage)).into(holder.imageRealEstate, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onError(Exception e) {
                holder.progressBar.setVisibility(View.GONE);

            }
        });
        holder.imageRealEstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Boolean.valueOf(add)) {
                    erAseImage(context, estateImage,position);
                }
            }
        });
         holder.details_description.setText(listDescription.get(position));
    }

    private void erAseImage(Context context, String estateImage, int position) {
        AlertDialog alertDialog = eraseImageAlertDIalg(context,estateImage,position);
        alertDialog.show();
    }


    private AlertDialog eraseImageAlertDIalg(final Context context, final String estateImage, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.input_erase, null);
        builder.setView(view).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                liste.remove(estateImage);
                listDescription.remove(position);
                notifyDataSetChanged();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        return builder.create();
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
        private ImageView imageRealEstate;
        private TextView details_description;
        private ProgressBar progressBar;
        public LeHolder(@NonNull View itemView) {
            super(itemView);
            progressBar=itemView.findViewById(R.id.progress_bar_imgerow);
            imageRealEstate = itemView.findViewById(R.id.imageRealEstate);
            details_description= itemView.findViewById(R.id.details_description);
        }
    }


}
