package nc.unc.ktrochon.festivalnotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import nc.unc.ktrochon.festivalnotification.entity.DetailsDuConcert;
import nc.unc.ktrochon.festivalnotification.fragment.DescriptionFragment;
import nc.unc.ktrochon.festivalnotification.fragment.ImageFragment;
import nc.unc.ktrochon.festivalnotification.fragment.YoutubeFragment;
import nc.unc.ktrochon.festivalnotification.notification.ConcertNotification;
import nc.unc.ktrochon.festivalnotification.repository.NotificationDatabase;

public class DescriptionDuConcertActivity extends AppCompatActivity {

    TextView artiste = null;
    TextView scene = null;
    TextView jour = null;
    TextView heure = null;
    ImageView imageView = null;
    DetailsDuConcert detailConcert = null;
    NotificationDatabase database = null;
    private String nomDuGroupe;
    private BottomNavigationView bottomNavigationView;
    private YoutubeFragment youtubeFragment = new YoutubeFragment();
    private ImageFragment imageFragment = new ImageFragment();
    private DescriptionFragment descriptionFragment = new DescriptionFragment();
    private BottomNavigationView navigationView;
    private String description;
    private String addressWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_du_concert);
        artiste = (TextView) findViewById(R.id.nom_Artiste);
        scene = (TextView) findViewById(R.id.Concert_Scene);
        heure = (TextView) findViewById(R.id.Concert_Heure);
        imageView = (ImageView) findViewById(R.id.Concert_Image);
        jour = (TextView) findViewById(R.id.Concert_Jour);
        ConcertNotification concertNotification = new ConcertNotification(DescriptionDuConcertActivity.this);
        nomDuGroupe = getIntent().getStringExtra("nomGroupe");
        /*favori.setOnClickListener(new View.OnClickListener() {
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
        });*/
        bottomNavigationView = findViewById(R.id.navigation_view);
        navigationView = findViewById(R.id.navigation_description_view);
        //getSupportFragmentManager().beginTransaction().replace(R.id.container_layout,homeFragment).commit();

        DescriptionDuConcertActivity.this.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Intent intent = new Intent(DescriptionDuConcertActivity.this,MainActivity.class);
                        DescriptionDuConcertActivity.this.startActivity(intent);
                        break;
                    case R.id.facebook:
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(addressWeb));
                        DescriptionDuConcertActivity.this.startActivity(intent1);
                        break;
                    case R.id.youtube:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_layout,youtubeFragment).commit();
                        return true;
                    case R.id.notification:
                        item.setIcon(R.drawable.ic_done);
                        concertNotification.notify(1,false, "My concert notification",artiste.getText().toString());
                        return true;
                }
                return false;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container_description_menu,imageFragment).commit();

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
                        URL url = new URL("https://daviddurand.info/D228/festival/info/"+nomDuGroupe);
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
                                description = detailConcert.getData().getTexte();
                                DescriptionDuConcertActivity.this.navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                                    @Override
                                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                        switch (item.getItemId()){
                                            case R.id.image_groupe:
                                                getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.container_description_menu,imageFragment)
                                                        .commit();
                                                return true;
                                            case R.id.description_groupe:
                                                //DescriptionFragment fragment = (DescriptionFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_description);
                                                getSupportFragmentManager().beginTransaction()
                                                        .add(R.id.container_description_menu,DescriptionFragment.newInstance(description),DescriptionFragment.class.getName())
                                                        .commit();
                                                return true;
                                        }
                                        return false;
                                    }
                                });
                                addressWeb = detailConcert.getData().getWeb();
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

    public String getDescription() {
        return description;
    }
}