package nc.unc.ktrochon.festivalnotification.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import androidx.room.Room;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
import nc.unc.ktrochon.festivalnotification.repository.NotificationDatabase;

public class MyAdapterMainActivity extends RecyclerView.Adapter<MyAdapterMainActivity.ViewHolder> implements Filterable {
    private ListeDesConcerts listeDesConcerts;
    private Context context;
    private boolean favori;
    private Bitmap bitmap;
    private List<FavoriConcert> favoris;
    private HttpsURLConnection connectionPhoto = null;
    private InputStream inputStream = null;
    private static final String API_URL = "https://daviddurand.info/D228/festival/illustrations/";
    private static final String END_URL = "/image.jpg";
    private List<DetailsDuConcert> festival = new ArrayList<>();
    private DetailsDuConcert detailsDuConcert = null;
    private String nomConcert;
    private ProgressDialog progressDialog;

    public MyAdapterMainActivity(ListeDesConcerts listeDesConcerts, Context context) {
        this.listeDesConcerts = listeDesConcerts;
        this.context = context;
    }

    public void setListeDesConcerts(ListeDesConcerts listeDesConcerts) {
        this.listeDesConcerts = listeDesConcerts;
        notifyDataSetChanged();
    }

    public void setFavoris(List<FavoriConcert> favoris) {
        this.favoris = favoris;
        notifyDataSetChanged();
    }

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_groupe,parent,false);
        return new ViewHolder(itemView);
    }

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

    @Override
    public int getItemCount() {
        if (listeDesConcerts.getData()==null){
            return 0;
        }
        List<String> maListe = Arrays.asList(listeDesConcerts.getData());
        return maListe.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                /*progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Chargement en cours...");
                progressDialog.setCancelable(false);
                progressDialog.show();*/
                List<DetailsDuConcert> listeATrier = getDetailsForAll();
                String[] sorted = new String[listeATrier == null ? 0 : listeATrier.size()];
                int index = 0;
                String jour;
                String scene;
                switch (charSequence.toString()){
                    case "null-Acoustique":
                        scene = charSequence.toString().split("-")[0];
                        index = 0;
                        for (DetailsDuConcert detail:listeATrier) {
                            if (detail.getData().getScene().equalsIgnoreCase(scene)){
                                sorted[index] = detail.getData().getArtiste().replace(" ","-").toLowerCase(Locale.ROOT);
                                index ++;
                            }
                        }
                        break;
                    case "null-Amplifiée":
                        scene = charSequence.toString().split("-")[0];
                        index = 0;
                        for (DetailsDuConcert detail:listeATrier) {
                            if (detail.getData().getScene().equalsIgnoreCase(scene)){
                                sorted[index] = detail.getData().getArtiste().replace(" ","-").toLowerCase(Locale.ROOT);
                                index ++;
                            }
                        }
                        break;
                    case "null-Vendredi":
                        jour = charSequence.toString().split("-")[1];
                        index = 0;
                        for (DetailsDuConcert detail:listeATrier) {
                            if (detail.getData().getJour().equalsIgnoreCase(jour)){
                                sorted[index] = detail.getData().getArtiste().replace(" ","-").toLowerCase(Locale.ROOT);
                                index ++;
                            }
                        }
                        break;
                    case "null-Samedi":
                        jour = charSequence.toString().split("-")[1];
                        index = 0;
                        for (DetailsDuConcert detail:listeATrier) {
                            if (detail.getData().getJour().equalsIgnoreCase(jour)){
                                sorted[index] = detail.getData().getArtiste().replace(" ","-").toLowerCase(Locale.ROOT);
                                index ++;
                            }
                        }
                        break;
                    case "Acoustique-Vendredi":
                        scene = charSequence.toString().split("-")[0];
                        jour = charSequence.toString().split("-")[1];
                        index = 0;
                        for (DetailsDuConcert detail:listeATrier) {
                            if (detail.getData().getScene().equalsIgnoreCase(scene) && detail.getData().getJour().equalsIgnoreCase(jour)){
                                sorted[index] = detail.getData().getArtiste().replace(" ","-").toLowerCase(Locale.ROOT);
                                index ++;
                            }
                        }
                        break;
                    case "Acoustique-Samedi":scene = charSequence.toString().split("-")[0];
                        jour = charSequence.toString().split("-")[1];
                        index = 0;
                        for (DetailsDuConcert detail:listeATrier) {
                            if (detail.getData().getScene().equalsIgnoreCase(scene) && detail.getData().getJour().equalsIgnoreCase(jour)){
                                sorted[index] = detail.getData().getArtiste().replace(" ","-").toLowerCase(Locale.ROOT);
                                index ++;
                            }
                        }
                        break;
                    case "Amplifiée-Vendredi":scene = charSequence.toString().split("-")[0];
                        jour = charSequence.toString().split("-")[1];
                        index = 0;
                        for (DetailsDuConcert detail:listeATrier) {
                            if (detail.getData().getScene().equalsIgnoreCase(scene) && detail.getData().getJour().equalsIgnoreCase(jour)){
                                sorted[index] = detail.getData().getArtiste().replace(" ","-").toLowerCase(Locale.ROOT);
                                index ++;
                            }
                        }
                        break;
                    case "Amplifiée-Samedi":scene = charSequence.toString().split("-")[0];
                        jour = charSequence.toString().split("-")[1];
                        index = 0;
                        for (DetailsDuConcert detail:listeATrier) {
                            if (detail.getData().getScene().equalsIgnoreCase(scene) && detail.getData().getJour().equalsIgnoreCase(jour)){
                                sorted[index] = detail.getData().getArtiste().replace(" ","-").toLowerCase(Locale.ROOT);
                                index ++;
                            }
                        }
                        break;
                    default:
                        break;
                }
                FilterResults results = new FilterResults();
                listeDesConcerts.setData(sorted);
                results.count = listeDesConcerts.getData().length;
                results.values = listeDesConcerts.getData();
                return results;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                charSequence.toString();
                String.valueOf(filterResults.count);
                listeDesConcerts.setData((String[]) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        CardView cardView = itemView.findViewById(R.id.card_view_listgroupe);
        TextView groupView = cardView.findViewById(R.id.textgroup_view);
        ImageView imageGroup = cardView.findViewById(R.id.imagegroup_view);
        ImageView imageFavori = cardView.findViewById(R.id.nomgroup_view);
    }

    public boolean isFavori() {
        return favori;
    }

    private List<DetailsDuConcert> getDetailsForAll(){
        HttpsURLConnection connection = null;
        if (listeDesConcerts.getData() == null){
            return null;
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
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return festival;
    }
}
