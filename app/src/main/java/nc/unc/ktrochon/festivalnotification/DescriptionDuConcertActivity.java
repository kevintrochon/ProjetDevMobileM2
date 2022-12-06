package nc.unc.ktrochon.festivalnotification;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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
import nc.unc.ktrochon.festivalnotification.entity.FavoriConcert;
import nc.unc.ktrochon.festivalnotification.fragment.DescriptionFragment;
import nc.unc.ktrochon.festivalnotification.fragment.ImageFragment;
import nc.unc.ktrochon.festivalnotification.helper.NetworkHelper;
import nc.unc.ktrochon.festivalnotification.notification.ConcertNotification;
import nc.unc.ktrochon.festivalnotification.repository.NotificationDatabase;

public class DescriptionDuConcertActivity extends AppCompatActivity {
    private static final String TAG = "DescriptionDuConcertAct";
    private static final int IS_FAVORI = 1;
    public static final String APIREST = "https://daviddurand.info/D228/festival/info/";
    public static final String REQUEST_METHOD = "GET";
    public static final int SECOND = 1000;
    public static final String DATABASENAME = "festival-notification";
    private TextView artiste = null;
    private TextView scene = null;
    private TextView jour = null;
    private TextView heure = null;
    private ImageView imageView = null;
    private DetailsDuConcert detailConcert = null;
    private NotificationDatabase database = null;
    private String nomDuGroupe;
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView navigationView;
    private String description;
    private String addressWeb;
    private final FavoriConcert myfavoriConcert = new FavoriConcert();
    private int temps;

    @SuppressLint({"RestrictedApi", "NonConstantResourceId"})
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
        database = Room.databaseBuilder(getApplicationContext(), NotificationDatabase.class, DATABASENAME).build();
        bottomNavigationView = findViewById(R.id.navigation_view);
        navigationView = findViewById(R.id.navigation_description_view);
        boolean isfavori = getIntent().getBooleanExtra("favori",false);
        if (isfavori){
            bottomNavigationView.getMenu().findItem(R.id.notification).setIcon(R.drawable.ic_done);
        }else {
            bottomNavigationView.getMenu().findItem(R.id.notification).setIcon(R.drawable.ic_notifications);
        }
        DescriptionDuConcertActivity.this.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    Intent intent = new Intent(DescriptionDuConcertActivity.this,MainActivity.class);
                    DescriptionDuConcertActivity.this.startActivity(intent);
                    break;
                case R.id.facebook:
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(addressWeb));
                    DescriptionDuConcertActivity.this.startActivity(intent1);
                    break;
                case R.id.notification:
                    if (myfavoriConcert.getIsFavori()==IS_FAVORI){
                        item.setIcon(R.drawable.ic_notifications);
                        myfavoriConcert.setIsFavori(IS_FAVORI);
                        myfavoriConcert.setArtiste(nomDuGroupe);
                        myfavoriConcert.setHeure(heure.toString());
                        myfavoriConcert.setJours(jour.toString());
                        myfavoriConcert.setTime(""+detailConcert.getData().getTime());
                        new Thread(() -> database.favoriDAO().deleteFavori(myfavoriConcert)).start();
                    }
                    else {

                        Handler handler = new Handler();
                        handler.postDelayed(() -> concertNotification.notify(1,false, "My concert notification",artiste.getText().toString()),temps);
                        myfavoriConcert.setIsFavori(IS_FAVORI);
                        myfavoriConcert.setArtiste(nomDuGroupe);
                        myfavoriConcert.setHeure(heure.toString());
                        myfavoriConcert.setJours(jour.toString());
                        myfavoriConcert.setTime(""+detailConcert.getData().getTime());
                        item.setIcon(R.drawable.ic_done);
                        new Thread(() -> database.favoriDAO().insertFavori(myfavoriConcert)).start();
                    }
                    return true;
            }
            return false;
        });

    }
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onResume() {
        super.onResume();

        new Thread(() -> {
            HttpsURLConnection connection = null;
            InputStream inputStream = null;
            try {
                if (NetworkHelper.isNetworkAvailable(DescriptionDuConcertActivity.this)) {
                    URL url = new URL(APIREST +nomDuGroupe);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod(REQUEST_METHOD);
                    inputStream = new BufferedInputStream(connection.getInputStream());
                    Scanner scanner = new Scanner(inputStream);
                    Genson genson = new GensonBuilder().useConstructorWithArguments(true).create();
                    detailConcert = genson.deserialize(scanner.nextLine(),DetailsDuConcert.class);
                    temps = detailConcert.getData().getTime() * SECOND;
                    database = Room.databaseBuilder(getApplicationContext(), NotificationDatabase.class,"festival-notification").build();
                    FavoriConcert favoriConcert = database.favoriDAO().loadById(nomDuGroupe);
                    runOnUiThread(() -> {
                        artiste.setText(detailConcert.getData().getArtiste());
                        description = detailConcert.getData().getTexte();
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.container_description_menu,DescriptionFragment.newInstance(description),DescriptionFragment.class.getName())
                                .commit();
                        DescriptionDuConcertActivity.this.navigationView.setOnItemSelectedListener(item -> {
                            switch (item.getItemId()){
                                case R.id.image_groupe:
                                    getSupportFragmentManager().beginTransaction()
                                            .add(R.id.container_description_menu,ImageFragment.newInstance(detailConcert.getData().getImage()),ImageFragment.class.getName())
                                            .commit();
                                    return true;
                                case R.id.description_groupe:
                                    getSupportFragmentManager().beginTransaction()
                                            .add(R.id.container_description_menu,DescriptionFragment.newInstance(description),DescriptionFragment.class.getName())
                                            .commit();
                                    return true;
                            }
                            return false;
                        });
                        addressWeb = detailConcert.getData().getWeb();
                        scene.setText(detailConcert.getData().getScene());
                        jour.setText(detailConcert.getData().getJour());
                        heure.setText(detailConcert.getData().getHeure());
                        if (favoriConcert != null){
                            myfavoriConcert.setArtiste(favoriConcert.getArtiste());
                            myfavoriConcert.setJours(favoriConcert.getJours());
                            myfavoriConcert.setHeure(favoriConcert.getHeure());
                            myfavoriConcert.setTime(favoriConcert.getTime());
                            myfavoriConcert.setIsFavori(favoriConcert.getIsFavori());
                        }
                    });
                }
                if(inputStream != null)inputStream.close();
                if(connection != null) connection.disconnect();
            } catch (Exception e) {
                Log.e("Exchange-JSON :","Connot found the API", e);
            }
            finally {
                if (connection != null){
                    connection.disconnect();
                }
            }
        }).start();
    }

}