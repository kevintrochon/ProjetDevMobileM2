package nc.unc.ktrochon.festivalnotification.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import nc.unc.ktrochon.festivalnotification.R;
import nc.unc.ktrochon.festivalnotification.entity.FavoriConcert;
import nc.unc.ktrochon.festivalnotification.entity.ListeDesConcerts;

public class MyAdapterMainActivity extends RecyclerView.Adapter<MyAdapterMainActivity.ViewHolder> {
    private ListeDesConcerts listeDesConcerts;
    View.OnClickListener listener;
    private boolean favori;
    private Bitmap bitmap;
    private List<FavoriConcert> favoris;
    HttpsURLConnection connectionPhoto = null;
    InputStream inputStream = null;
    private static final String API_URL = "https://daviddurand.info/D228/festival/illustrations/";
    private static final String END_URL = "/image.jpg";

    public MyAdapterMainActivity(ListeDesConcerts listeDesConcerts, View.OnClickListener listener,List<FavoriConcert> favoris) {
        this.listeDesConcerts = listeDesConcerts;
        this.listener = listener;
        this.favoris = favoris;
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cardView.setTag((position));
        List<String> maListe = Arrays.asList(listeDesConcerts.getData());
        String nomConcert = maListe.get(position);
        favori = getMyFavorit(nomConcert);
        holder.groupView.setText(nomConcert);
        holder.cardView.setOnClickListener(listener);
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
        List<String> maListe = Arrays.asList(listeDesConcerts.getData());
        return maListe.size();
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
}
