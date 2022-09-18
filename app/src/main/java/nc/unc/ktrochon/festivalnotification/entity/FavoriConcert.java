package nc.unc.ktrochon.festivalnotification.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class FavoriConcert {
    @PrimaryKey
    int favoriId;
    @ColumnInfo(name = "nom")
    String artiste;
    String time;
    String heure;
    String Jours;
    boolean isFavori;

    public FavoriConcert(String artiste, String time, String heure, String jours, boolean isFavori) {
        this.artiste = artiste;
        this.time = time;
        this.heure = heure;
        Jours = jours;
        this.isFavori = isFavori;
    }

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

    public boolean isFavori() {
        return isFavori;
    }

    public void setFavori(boolean favori) {
        isFavori = favori;
    }

    @Override
    public String toString() {
        return "FavoriConcert{" +
                "artiste='" + artiste + '\'' +
                ", time='" + time + '\'' +
                ", heure='" + heure + '\'' +
                ", Jours='" + Jours + '\'' +
                ", isFavori=" + isFavori +
                '}';
    }
}
