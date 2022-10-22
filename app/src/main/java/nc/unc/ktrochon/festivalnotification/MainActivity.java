package nc.unc.ktrochon.festivalnotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.owlike.genson.Genson;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import nc.unc.ktrochon.festivalnotification.adapter.MyAdapterMainActivity;
import nc.unc.ktrochon.festivalnotification.adapter.MySpinnerAdapter;
import nc.unc.ktrochon.festivalnotification.entity.FavoriConcert;
import nc.unc.ktrochon.festivalnotification.entity.ListeDesConcerts;
import nc.unc.ktrochon.festivalnotification.repository.NotificationDatabase;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private MyAdapterMainActivity adapter;
    private ListeDesConcerts festival;
    private NotificationDatabase database;
    private boolean isFavori;
    private RecyclerView recyclerView;
    private Bitmap bitmap = null;
    private String choixUtilisateurFinal;
    private String choixSceneUtilisateur;
    private String choixJourUtilisateur;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        List<String> choixScene = new ArrayList<>();
        choixScene.add("Scene");
        choixScene.add("Amplifiée");
        choixScene.add("Acoustique");
        Spinner sceneSpinner = (Spinner) findViewById(R.id.spinner_scene);
        MySpinnerAdapter sceneSpinnerAdapter = new MySpinnerAdapter(this,choixScene,R.layout.item_spinner,R.id.possibilite_filtre);
        sceneSpinner.setAdapter(sceneSpinnerAdapter);

        sceneSpinner.setOnItemSelectedListener(this);

        List<String> choixJour = new ArrayList<>();
        choixJour.add("Jour");
        choixJour.add("Vendredi");
        choixJour.add("Samedi");
        Spinner jourSpinner = (Spinner) findViewById(R.id.spinner_jour);
        MySpinnerAdapter jourSpinnerAdapter = new MySpinnerAdapter(this,choixJour,R.layout.item_spinner,R.id.possibilite_filtre);
        jourSpinner.setAdapter(jourSpinnerAdapter);
        jourSpinner.setOnItemSelectedListener(this);
        recyclerView = findViewById(R.id.groupes_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
    }

    private void getDetaisl(String nomGroupe) {
        Intent intent = new Intent(this, DescriptionDuConcertActivity.class);
        intent.putExtra("nomGroupe",nomGroupe);
        isFavori = this.adapter.getMyFavorit(nomGroupe);
        intent.putExtra("favori",isFavori);
        this.startActivity(intent);
    }

    protected void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        initViews();
        pd.hide();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void initViews() {
        pd = new ProgressDialog(this);
        pd.setMessage("Chargement en cours...");
        pd.setCancelable(false);
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
                HttpsURLConnection connectionPhoto = null;
                InputStream inputStream = null;

                try {
                    if (isNetworkAvailable()) {
                        URL url = new URL("https://daviddurand.info/D228/festival/liste");
                        connection = (HttpsURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        inputStream = new BufferedInputStream(connection.getInputStream());
                        Scanner scanner = new Scanner(inputStream);
                        Genson genson = new Genson();
                        festival = genson.deserialize(scanner.nextLine(),ListeDesConcerts.class);
                        database = Room.databaseBuilder(getApplicationContext(),NotificationDatabase.class,"festival-notification").build();
                        List<FavoriConcert> myFavori = database.favoriDAO().loadAll();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.adapter = new MyAdapterMainActivity(festival, MainActivity.this,myFavori);
                                recyclerView.setAdapter(MainActivity.this.adapter);
                                pd.hide();
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (MainActivity.this.adapter != null){
            if (adapterView.getSelectedView().getId()==R.id.spinner_scene){
                choixSceneUtilisateur = adapterView.getItemAtPosition(i).toString();
            }else {
                choixJourUtilisateur = adapterView.getItemAtPosition(i).toString();
            }
            choixUtilisateurFinal = choixSceneUtilisateur +"-"+choixJourUtilisateur;
            MainActivity.this.adapter.getFilter().filter(choixUtilisateurFinal);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}