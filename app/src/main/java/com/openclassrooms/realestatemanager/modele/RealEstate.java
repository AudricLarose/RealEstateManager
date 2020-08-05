package com.openclassrooms.realestatemanager.modele;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity(tableName = "bdd")
public class RealEstate implements Serializable {

    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "id")
    private int id;

    private String type;
    private String adresse;
    private Integer postal;
    private String town;
    private String description;
    private String nomAgent;

    private Integer chambre;
    private Integer piece;
    private Integer sdb;
    private Integer surface;
    private String market;
    private Integer prix;
    private Integer numberPhotos;

    private String ischecked;
    private Double lattitude;
    private Double longitude;
    private String url;
    private String inEuro;
    private String selled;
    private String tempInsert = "false";
    private String tempUpdate = "false";


    public RealEstate() {
    }

    public RealEstate(String inEuro, String ischecked, String type, String nomAgent, String adresse, Integer chambre, String description, String market, Integer postal, Integer piece, Integer prix, Integer sdb, Integer surface, String town, String selled, Double latitude, Double longitude, String url) {
        this.type = type;
        this.adresse = adresse;
        this.postal = postal;
        this.town = town;
        this.description = description;
        this.nomAgent = nomAgent;
        this.chambre = chambre;
        this.piece = piece;
        this.sdb = sdb;
        this.surface = surface;
        this.market = market;
        this.prix = prix;
        this.ischecked = ischecked;
        this.lattitude = latitude;
        this.longitude = longitude;
        this.url = url;
        this.inEuro = inEuro;
        this.selled = selled;
    }


    public RealEstate(String inEuro, String ischecked, String type, String nomAgent, String adresse, int chambre, String description, String market, int postal, int piece, int prix, int sdb, int surface, String town, String selled, double longitude, double lattitude, String url) {
        this.type = type;
        this.adresse = adresse;
        this.postal = postal;
        this.town = town;
        this.description = description;
        this.nomAgent = nomAgent;
        this.chambre = chambre;
        this.piece = piece;
        this.sdb = sdb;
        this.surface = surface;
        this.market = market;
        this.prix = prix;
        this.ischecked = ischecked;
        this.longitude = longitude;
        this.lattitude = lattitude;
        this.url = url;
        this.inEuro = inEuro;
        this.selled = selled;
    }

    public static RealEstate fromContentValues(ContentValues contentValues) {
        RealEstate estate = new RealEstate();
        if (contentValues.containsKey("id")) {
            estate.setId(contentValues.getAsInteger("id"));
        }
        if (contentValues.containsKey("type")) {
            estate.setType(contentValues.getAsString("type"));
        }
        if (contentValues.containsKey("adresse")) {
            estate.setAdresse(contentValues.getAsString("adresse"));
        }
        if (contentValues.containsKey("postal")) {
            estate.setTown(contentValues.getAsString("town"));
        }
        if (contentValues.containsKey("description")) {
            estate.setDescription(contentValues.getAsString("description"));
        }
        if (contentValues.containsKey("nomAgent")) {
            estate.setNomAgent(contentValues.getAsString("nomAgent"));
        }
        if (contentValues.containsKey("chambre")) {
            estate.setChambre(contentValues.getAsInteger("chambre"));
        }
        if (contentValues.containsKey("piece")) {
            estate.setPiece(contentValues.getAsInteger("piece"));
        }
        if (contentValues.containsKey("sdb")) {
            estate.setSdb(contentValues.getAsInteger("sdb"));
        }
        if (contentValues.containsKey("surface")) {
            estate.setSurface(contentValues.getAsInteger("surface"));
        }
        if (contentValues.containsKey("market")) {
            estate.setMarket(contentValues.getAsString("market"));
        }
        if (contentValues.containsKey("selled")) {
            estate.setMarket(contentValues.getAsString("selled"));
        }
        if (contentValues.containsKey("prix")) {
            estate.setPrix(contentValues.getAsInteger("prix"));
        }
        if (contentValues.containsKey("ischecked")) {
            estate.setIschecked((contentValues.getAsString("ischecked")));
        }
        if (contentValues.containsKey("lattitude")) {
            estate.setLattitude(contentValues.getAsDouble("lattitude"));
        }
        if (contentValues.containsKey("longitude")) {
            estate.setLattitude(contentValues.getAsDouble("longitude"));
        }
        if (contentValues.containsKey("url")) {
            estate.setMarket(contentValues.getAsString("url"));
        }
        if (contentValues.containsKey("inEuro")) {
            estate.setInEuro(contentValues.getAsString("inEuro"));
        }
        return estate;

    }

    public Integer getNumberPhotos() {
        return numberPhotos;
    }

    public void setNumberPhotos(Integer numberPhotos) {
        this.numberPhotos = numberPhotos;
    }

    public String getTempUpdate() {
        return tempUpdate;
    }

    public void setTempUpdate(String tempUpdate) {
        this.tempUpdate = tempUpdate;
    }


    public String getSelled() {
        return selled;
    }

    public void setSelled(String selled) {
        this.selled = selled;
    }


    public String getInEuro() {
        return inEuro;
    }

    public void setInEuro(String inEuro) {
        this.inEuro = inEuro;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNomAgent() {
        return nomAgent;
    }

    public void setNomAgent(String nomAgent) {
        this.nomAgent = nomAgent;
    }

    public String getIschecked() {
        return ischecked;
    }

    public void setIschecked(String ischecked) {
        this.ischecked = ischecked;
    }

    public String getTempInsert() {
        return tempInsert;
    }

    public void setTempInsert(String tempInsert) {
        this.tempInsert = tempInsert;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Integer getPrix() {
        return prix;
    }

    public void setPrix(Integer prix) {
        this.prix = prix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Integer getPostal() {
        return postal;
    }

    public void setPostal(Integer postal) {
        this.postal = postal;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getChambre() {
        return chambre;
    }

    public void setChambre(Integer chambre) {
        this.chambre = chambre;
    }

    public Integer getPiece() {
        return piece;
    }

    public void setPiece(Integer piece) {
        this.piece = piece;
    }

    public Integer getSdb() {
        return sdb;
    }

    public void setSdb(Integer sdb) {
        this.sdb = sdb;
    }

    public Integer getSurface() {
        return surface;
    }

    public void setSurface(Integer surface) {
        this.surface = surface;
    }
}
