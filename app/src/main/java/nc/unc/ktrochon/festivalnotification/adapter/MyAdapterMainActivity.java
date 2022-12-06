package nc.unc.ktrochon.festivalnotification.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import nc.unc.ktrochon.festivalnotification.DescriptionDuConcertActivity;
import nc.unc.ktrochon.festivalnotification.R;
import nc.unc.ktrochon.festivalnotification.entity.DetailsDuConcert;
import nc.unc.ktrochon.festivalnotification.entity.FavoriConcert;
import nc.unc.ktrochon.festivalnotification.entity.ListeDesConcerts;

/**
 * Adapteur qui permet d afficher la liste des concerts concerts dans un Recycler View et celui ci est filtrable.
 */
public class MyAdapterMainActivity extends RecyclerView.Adapter<MyAdapterMainActivity.ViewHolder> implements Filterable {
    private static final int DebutMotJour = 0;
    private static final int FinMotJour = 6;
    private static final int DebutMotScene = 6;
    private ListeDesConcerts listeDesConcerts;
    private Context context;
    private boolean favori;
    private Bitmap bitmap;
    private List<FavoriConcert> favoris;
    private HttpsURLConnection connectionPhoto = null;
    private InputStream inputStream = null;
    private static final String API_URL = "https://daviddurand.info/D228/festival/illustrations/";
    private static final String END_URL = "/image.jpg";
    private final List<DetailsDuConcert> festival = new ArrayList<>();
    private DetailsDuConcert detailsDuConcert = null;
    private String nomConcert;

    /**
     * Constructeur de l adapteur de la vue.
     * @param listeDesConcerts la liste des concerts qui sera afficher.
     * @param context le context sur lequel l adapter doit agir.
     */
    public MyAdapterMainActivity(ListeDesConcerts listeDesConcerts, Context context) {
        this.listeDesConcerts = listeDesConcerts;
        this.context = context;
    }

    /**
     * Getter de l attribut liste des concerts.
     * @param listeDesConcerts nouvelle liste des concerts.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setListeDesConcerts(ListeDesConcerts listeDesConcerts) {
        this.listeDesConcerts = listeDesConcerts;
        notifyDataSetChanged();
    }

    /**
     * Setter de la liste des favoris.
     * @param favoris nouvelle liste des favoris.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setFavoris(List<FavoriConcert> favoris) {
        this.favoris = favoris;
        notifyDataSetChanged();
    }

    /**
     * Method qui permet de savoir si un concert est en favori.
     * @param nomConcert nom du concert ou l on souhaite comparer
     * @return true si les noms du concert correspondent et que le champ favori est egale a 1.
     */
    public boolean getMyFavorit(String nomConcert){
        boolean isFavori = false;
        for (FavoriConcert f:favoris
             ) {
            if (f.getArtiste().equalsIgnoreCase(nomConcert) && f.getIsFavori()==1){
                isFavori = true;
                break;
            }
        }
        return isFavori;
    }

    /**
     * Method qui est appelee lors de la creation de l adapter
     * @param parent Vue parent.
     * @param viewType l id de la vue.
     * @return une vue.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_groupe,parent,false);
        return new ViewHolder(itemView);
    }

    /**
     * Method qui permet de lier les elements a la vue.
     * @param holder View qui contient tout les elements graphiques.
     * @param position position de l element a mapper dans la liste.
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        holder.cardView.setTag((position));
        List<String> maListe = Arrays.asList(listeDesConcerts.getData());
        nomConcert = maListe.get(position);
        favori = getMyFavorit(nomConcert);
        holder.groupView.setText(nomConcert);
        holder.cardView.setOnClickListener(view -> {
            TextView textView = view.findViewById(R.id.textgroup_view);
            String texte = textView.getText().toString();
            Intent intent = new Intent(context, DescriptionDuConcertActivity.class);
            intent.putExtra("nomGroupe",texte);
            Boolean isFavori = getMyFavorit(texte);
            intent.putExtra("favori",isFavori);
            ((Activity) context).startActivityForResult(intent,1);
        });
        try {
            URL urlPhoto = new URL(API_URL + nomConcert + END_URL);
            connectionPhoto = (HttpsURLConnection) urlPhoto.openConnection();
            if (connectionPhoto.getResponseCode()== HttpURLConnection.HTTP_OK) {
                inputStream = new BufferedInputStream(connectionPhoto.getInputStream());
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connectionPhoto != null){
                connectionPhoto.disconnect();
            }
        }

        holder.imageGroup.setImageBitmap(bitmap);
        // ajout de l'image si favori
        if (favori){
            holder.imageFavori.setImageResource(R.drawable.favori);
        }

    }

    /**
     * method qui retourne la taille de la liste.
     * @return le nombre d element contenu dans la liste a afficher.
     */
    @Override
    public int getItemCount() {
        if (listeDesConcerts.getData()==null){
            return 0;
        }
        List<String> maListe = Arrays.asList(listeDesConcerts.getData());
        return maListe.size();
    }

    /**
     * method qui permet de filtrer les elements de la vue
     * @return la liste des concerts filtree.
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                switch (charSequence.toString()){
                    case "Vendredi":
                    case "VendrediAmplifiée":
                        listeDesConcerts.setData(getListFilterByDay("Vendredi"));
                        break;
                    case "Samedi":
                        listeDesConcerts.setData(getListFilterByDay(charSequence));
                        break;
                    case "Amplifiée":
                        listeDesConcerts.setData(getListFilterByScene(charSequence));
                        break;
                    case "Acoustique":
                    case "SamediAcoustique":
                        listeDesConcerts.setData(getListFilterByScene("Acoustique"));
                        break;
                    case "SamediAmplifiée":
                        listeDesConcerts.setData(getListFilterBySceneAndByDay(charSequence));
                        break;
                    default:
                        listeDesConcerts.setData(getListNoneFiltered());
                        break;
                }
                results.count = listeDesConcerts.getData()== null ? 0 : listeDesConcerts.getData().length;
                results.values = listeDesConcerts.getData();
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listeDesConcerts.setData((String[]) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    /**
     * methode de filtrage de la liste des concerts par la scene.
     * @param charSequence valeur du filtre a appliquer a la liste des concerts.
     * @return un tableau de String triee le filtre.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String[] getListFilterByScene(CharSequence charSequence) {
        List<DetailsDuConcert> listeATrier = getDetailsForAll();
        if(listeATrier == null) throw new AssertionError();
        return listeATrier.stream()
                // je filtre la liste par rapport a l attribut Scene.
                .filter(detailsDuConcert1 -> detailsDuConcert1.getData().getScene().equalsIgnoreCase(charSequence.toString()))
                // je modifie le nom du groupe / artiste et je retourne le resultat sous forme de tableau de chaine de caractere.
                .map(detailsDuConcert1 -> detailsDuConcert1.getData().getArtiste().replace(" ", "-").toLowerCase(Locale.ROOT)).toArray(String[]::new);
    }

    /**
     * Methode de filtrage de la liste des concerts par scene et par jour.
     * @param charSequence valeur du filtre a appliquer a la liste des concerts.
     * @return un tableau de chaine de caractere triee selon le filtre.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String[] getListFilterBySceneAndByDay(CharSequence charSequence) {
        String jourConcert = charSequence.subSequence(DebutMotJour, FinMotJour).toString();
        String typeSceneConcert = charSequence.subSequence(DebutMotScene, charSequence.length()).toString();
        List<DetailsDuConcert> listeATrier = getDetailsForAll();
        if (listeATrier == null) throw new AssertionError();
        return listeATrier.stream()
                // je filtre la liste par rapport a l attribut Scene et du Jour.
                .filter(detailsDuConcert1 -> detailsDuConcert1.getData().getScene().equalsIgnoreCase(typeSceneConcert)
                        && detailsDuConcert1.getData().getJour().equalsIgnoreCase(jourConcert))
                // je modifie le nom du groupe / artiste et je retourne le resultat sous forme de tableau de chaine de caractere.
                .map(detailsDuConcert1 -> detailsDuConcert1.getData().getArtiste().replace(" ", "-").toLowerCase(Locale.ROOT)).toArray(String[]::new);
    }

    /**
     * methode de filtrage de la liste des concerts par jour.
     * @param charSequence valeur du filtre a appliquer a la liste des concerts.
     * @return un tableau de chaine de caractere triee selon le filtre.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String[] getListFilterByDay(CharSequence charSequence) {
        List<DetailsDuConcert> listeATrier = getDetailsForAll();
        if (listeATrier == null) throw new AssertionError();
        return listeATrier.stream()
                // je filtre la liste par rapport a l attribut Jour.
                .filter(detailsDuConcert1 -> detailsDuConcert1.getData().getJour().equalsIgnoreCase(charSequence.toString()))
                // je modifie le nom du groupe / artiste et je retourne le resultat sous forme de tableau de chaine de caractere.
                .map(detailsDuConcert1 -> detailsDuConcert1.getData().getArtiste().replace(" ", "-").toLowerCase(Locale.ROOT)).toArray(String[]::new);
    }

    /**
     * Method par default quand le filtre ne trouve rien.
     * @return la liste non filtree
     */
    private String[] getListNoneFiltered(){
        if (listeDesConcerts == null) throw new AssertionError();
        return listeDesConcerts.getData();
    }

    /**
     * Vue qui contient tout les composant a adapter
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        CardView cardView = itemView.findViewById(R.id.card_view_listgroupe);
        TextView groupView = cardView.findViewById(R.id.textgroup_view);
        ImageView imageGroup = cardView.findViewById(R.id.imagegroup_view);
        ImageView imageFavori = cardView.findViewById(R.id.nomgroup_view);
    }

    /**
     * method qui permet de recuperer tous les details de tous les concerts
     * @return une liste des details des concerts.
     */
    private List<DetailsDuConcert> getDetailsForAll(){
        HttpsURLConnection connection = null;
        if (listeDesConcerts.getData() == null){
            return null;
        }
        if(festival.size() == 17){
            return festival;
        }
        try {
            for (String name: listeDesConcerts.getData()
                 ) {
                URL urlConcert = new URL("https://daviddurand.info/D228/festival/info/"+name);
                connection = (HttpsURLConnection) urlConcert.openConnection();
                connection.setRequestMethod("GET");
                inputStream = new BufferedInputStream(connection.getInputStream());
                Scanner scanner = new Scanner(inputStream);
                Genson genson = new GensonBuilder().useConstructorWithArguments(true).create();
                detailsDuConcert = genson.deserialize(scanner.nextLine(),DetailsDuConcert.class);
                festival.add(detailsDuConcert);
                inputStream.close();
                connection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return festival;
    }
}
