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

import javax.net.ssl.HttpsURLConnection;

import nc.unc.ktrochon.festivalnotification.R;
import nc.unc.ktrochon.festivalnotification.entity.FavoriConcert;
import nc.unc.ktrochon.festivalnotification.entity.ListeDesConcerts;

public class MyAdapterMainActivity extends RecyclerView.Adapter<MyAdapterMainActivity.ViewHolder> {

    private static final String API_URL = "https://daviddurand.info/D228/festival/illustrations/";
    private static final String END_URL = "/image.jpg";

    private ListeDesConcerts listeDesConcerts;
    View.OnClickListener listener;
    private boolean favori;

    public MyAdapterMainActivity(ListeDesConcerts listeDesConcerts, View.OnClickListener listener, boolean favori) {
        this.listeDesConcerts = listeDesConcerts;
        this.listener = listener;
        this.favori = favori;
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
        holder.groupView.setText(nomConcert);
        holder.cardView.setOnClickListener(listener);

        /**
         * récupération de l'image du groupe
         */
        new Thread (new Runnable() {

            @Override
            public void run() {
                HttpsURLConnection connection = null;
                InputStream inputStream = null;

                try {
                    URL url = new URL(API_URL + nomConcert + END_URL);
                    connection = (HttpsURLConnection) url.openConnection();

                    if (connection.getResponseCode()== HttpURLConnection.HTTP_OK) {
                        inputStream = new BufferedInputStream(connection.getInputStream());
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        holder.imageGroup.setImageBitmap(bitmap);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
        ImageView imageFavori = cardView.findViewById(R.id.imagefavori_view);
    }
}
