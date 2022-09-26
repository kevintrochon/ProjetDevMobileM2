package nc.unc.ktrochon.festivalnotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import nc.unc.ktrochon.festivalnotification.entity.DetailsDuConcert;
import nc.unc.ktrochon.festivalnotification.entity.FavoriConcert;
import nc.unc.ktrochon.festivalnotification.notification.ConcertNotification;
import nc.unc.ktrochon.festivalnotification.repository.NotificationDatabase;

public class DescriptionDuConcertActivity extends AppCompatActivity {

    TextView artiste = null;
    TextView description = null;
    TextView web = null;
    TextView scene = null;
    TextView jour = null;
    TextView heure = null;
    ImageView imageView = null;
    CheckBox favori = null;
    DetailsDuConcert detailConcert = null;
    NotificationDatabase database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_du_concert);
        artiste = (TextView) findViewById(R.id.Concert_Artiste);
        description = (TextView) findViewById(R.id.Concert_Description);
        web = (TextView) findViewById(R.id.Concert_Facebook);
        scene = (TextView) findViewById(R.id.Concert_Scene);
        heure = (TextView) findViewById(R.id.Concert_Heure);
        imageView = (ImageView) findViewById(R.id.Concert_Image);
        jour = (TextView) findViewById(R.id.Concert_Jour);
        favori = (CheckBox) findViewById(R.id.Concert_Favori);
        ConcertNotification concertNotification = new ConcertNotification(DescriptionDuConcertActivity.this);

        favori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favori.isChecked()){
                    concertNotification.notify(1,false, "My concert notification",artiste.getText().toString());
                    favori.setActivated(true);
                    new Thread(()->{
                        database = Room.databaseBuilder(getApplicationContext(), NotificationDatabase.class,"festival-notification").build();
                        FavoriConcert favoriConcert = new FavoriConcert();
                        favoriConcert.setFavoriId(1);
                        favoriConcert.setIsFavori(1);
                        favoriConcert.setArtiste(artiste.getText().toString());
                        favoriConcert.setHeure(heure.toString());
                        favoriConcert.setJours(jour.toString());
                        favoriConcert.setTime(""+detailConcert.getData().getTime());
                        database.favoriDAO().insertFavori(favoriConcert);
                    }).start();
                }else {
                    concertNotification.cancelNotification(1);
                    favori.setActivated(false);
                    new Thread(()->{
                        database = Room.databaseBuilder(getApplicationContext(), NotificationDatabase.class,"festival-notification").build();
                        FavoriConcert favoriConcert = new FavoriConcert();
                        favoriConcert.setFavoriId(1);
                        favoriConcert.setIsFavori(1);
                        favoriConcert.setArtiste(artiste.getText().toString());
                        favoriConcert.setHeure(heure.toString());
                        favoriConcert.setJours(jour.toString());
                        favoriConcert.setTime(""+detailConcert.getData().getTime());
                        database.favoriDAO().deleteFavori(favoriConcert);
                    }).start();
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
                InputStream inputStream = null;
                try {
                    if (isNetworkAvailable()) {
                        URL url = new URL("https://daviddurand.info/D228/festival/info/diesel-groove");
                        connection = (HttpsURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        inputStream = new BufferedInputStream(connection.getInputStream());
                        Scanner scanner = new Scanner(inputStream);
                        Genson genson = new GensonBuilder().useConstructorWithArguments(true).create();
                        detailConcert = genson.deserialize(scanner.nextLine(),DetailsDuConcert.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                artiste.setText(detailConcert.getData().getArtiste());
                                description.setText(detailConcert.getData().getTexte());
                                web.setText(detailConcert.getData().getWeb());
                                scene.setText(detailConcert.getData().getScene());
                                jour.setText(detailConcert.getData().getJour());
                                heure.setText(detailConcert.getData().getHeure());
                                //TODO PEnsez a ajouter une image par defaut.
                            }
                        });
                    }
                    inputStream.close();
                } catch (Exception e) {
                    Log.e("Exchange-JSON :","Connot found the API", e);
                }
                finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}