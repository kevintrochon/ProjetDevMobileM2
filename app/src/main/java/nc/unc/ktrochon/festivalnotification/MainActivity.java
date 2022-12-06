package nc.unc.ktrochon.festivalnotification;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.owlike.genson.Genson;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import nc.unc.ktrochon.festivalnotification.adapter.MyAdapterMainActivity;
import nc.unc.ktrochon.festivalnotification.adapter.MySpinnerAdapter;
import nc.unc.ktrochon.festivalnotification.entity.FavoriConcert;
import nc.unc.ktrochon.festivalnotification.entity.ListeDesConcerts;
import nc.unc.ktrochon.festivalnotification.helper.ChoixDuFiltre;
import nc.unc.ktrochon.festivalnotification.helper.NetworkHelper;
import nc.unc.ktrochon.festivalnotification.repository.NotificationDatabase;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    public static final String RESTAPI = "https://daviddurand.info/D228/festival/liste";
    public static final String REQUEST_METHOD = "GET";
    public static final String DATABASENAME = "festival-notification";

    private MyAdapterMainActivity adapter;
    private ListeDesConcerts festival;
    private NotificationDatabase database;
    private boolean isFavori;
    private RecyclerView recyclerView;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        List<String> choixScene = new ArrayList<>();
        choixScene.add(ChoixDuFiltre.Filtre.name());
        choixScene.add(ChoixDuFiltre.Amplifiée.name());
        choixScene.add(ChoixDuFiltre.Acoustique.name());
        choixScene.add(ChoixDuFiltre.Vendredi.name());
        choixScene.add(ChoixDuFiltre.Samedi.name());
        choixScene.add(ChoixDuFiltre.VendrediAmplifiée.name());
        choixScene.add(ChoixDuFiltre.VemdrediAcoustique.name());
        choixScene.add(ChoixDuFiltre.SamediAmplifiée.name());
        choixScene.add(ChoixDuFiltre.SamediAcoustique.name());
        Spinner sceneSpinner = findViewById(R.id.spinner_scene);
        MySpinnerAdapter sceneSpinnerAdapter = new MySpinnerAdapter(this,choixScene,R.layout.item_spinner,R.id.possibilite_filtre);
        sceneSpinner.setAdapter(sceneSpinnerAdapter);
        sceneSpinner.setOnItemSelectedListener(this);
        adapter = new MyAdapterMainActivity(new ListeDesConcerts(),this);
        recyclerView = findViewById(R.id.groupes_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    protected void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        initViews();
    }

    /**
     * Method d initialisation de la vue.
     */
    private void initViews() {
        pd = new ProgressDialog(this);
        pd.setMessage("Chargement en cours...");
        pd.setCancelable(false);
        pd.show();
        // Thread qui permet de recuperer les favoris et d initialiser la liste des concerts pour l adapteur.
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
                InputStream inputStream = null;
                try {
                    if (NetworkHelper.isNetworkAvailable(MainActivity.this)) {
                        URL url = new URL(RESTAPI);
                        connection = (HttpsURLConnection) url.openConnection();
                        connection.setRequestMethod(REQUEST_METHOD);
                        inputStream = new BufferedInputStream(connection.getInputStream());
                        Scanner scanner = new Scanner(inputStream);
                        Genson genson = new Genson();
                        database = Room.databaseBuilder(getApplicationContext(), NotificationDatabase.class, DATABASENAME).build();
                        List<FavoriConcert> favoriConcerts = database.favoriDAO().loadAll();
                        festival = genson.deserialize(scanner.nextLine(),ListeDesConcerts.class);
                        //methode qui renvoie le traitement du thread a la vue (method de callback).
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.adapter.setListeDesConcerts(festival);
                                MainActivity.this.adapter.setFavoris(favoriConcerts);
                                pd.hide();
                            }
                        });
                    }
                    inputStream.close();
                    connection.disconnect();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (MainActivity.this.adapter != null){
            MainActivity.this.adapter.getFilter().filter(adapterView.getItemAtPosition(i).toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }


}