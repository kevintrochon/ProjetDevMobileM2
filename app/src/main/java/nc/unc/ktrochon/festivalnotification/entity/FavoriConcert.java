package nc.unc.ktrochon.festivalnotification.entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "FavoriConcert")
public class FavoriConcert implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "nom")
    @NonNull
    String artiste;
    String time;
    String heure;
    String Jours;
    int isFavori;

    public String getArtiste() {
        return artiste;
    }

    public void setArtiste(String artiste) {
        this.artiste = artiste;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getJours() {
        return Jours;
    }

    public void setJours(String jours) {
        Jours = jours;
    }

    public int getIsFavori() {
        return isFavori;
    }

    public void setIsFavori(int isFavori) {
        this.isFavori = isFavori;
    }

    @Override
    public String toString() {
        return "FavoriConcert{" +
                ", artiste='" + artiste + '\'' +
                ", time='" + time + '\'' +
                ", heure='" + heure + '\'' +
                ", Jours='" + Jours + '\'' +
                ", isFavori=" + isFavori +
                '}';
    }
}
