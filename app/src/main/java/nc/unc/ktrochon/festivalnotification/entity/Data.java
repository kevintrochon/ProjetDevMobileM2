package nc.unc.ktrochon.festivalnotification.entity;

import java.util.Objects;

/**
 * Classe qui represente les details des concerts. pour la serialisation
 */
public class Data {
    private String artiste;
    private String texte;
    private String web;
    private String image;
    private String scene;
    private String jour;
    private String heure;
    private int time;

    public Data() {
    }

    public Data(String artiste, String texte, String web, String image, String scene, String jour, String heure, int time) {
        this.artiste = artiste;
        this.texte = texte;
        this.web = web;
        this.image = image;
        this.scene = scene;
        this.jour = jour;
        this.heure = heure;
        this.time = time;
    }

    public String getArtiste() {
        return artiste;
    }

    public void setArtiste(String artiste) {
        this.artiste = artiste;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return time == data.time && Objects.equals(artiste, data.artiste) && Objects.equals(texte, data.texte) && Objects.equals(web, data.web) && Objects.equals(image, data.image) && Objects.equals(scene, data.scene) && Objects.equals(jour, data.jour) && Objects.equals(heure, data.heure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artiste, texte, web, image, scene, jour, heure, time);
    }

    @Override
    public String toString() {
        return "Data{" +
                "artiste='" + artiste + '\'' +
                ", texte='" + texte + '\'' +
                ", web='" + web + '\'' +
                ", image='" + image + '\'' +
                ", scene='" + scene + '\'' +
                ", jour='" + jour + '\'' +
                ", heure='" + heure + '\'' +
                ", time=" + time +
                '}';
    }
}
