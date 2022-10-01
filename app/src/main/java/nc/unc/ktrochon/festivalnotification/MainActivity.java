package nc.unc.ktrochon.festivalnotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.owlike.genson.Genson;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import nc.unc.ktrochon.festivalnotification.adapter.MyAdapterMainActivity;
import nc.unc.ktrochon.festivalnotification.entity.FavoriConcert;
import nc.unc.ktrochon.festivalnotification.entity.ListeDesConcerts;
import nc.unc.ktrochon.festivalnotification.repository.NotificationDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MyAdapterMainActivity adapter;
    private TextView textView;
    private Button detailButton;
    private ListeDesConcerts festival;
    private NotificationDatabase database;
    private boolean isFavori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        textView = (TextView) findViewById(R.id.text_API);
        Genson genson = new Genson();
    }

    private void getDetaisl() {
        Intent intent = new Intent(this, DescriptionDuConcertActivity.class);
        this.startActivity(intent);
    }

    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
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
                        //TODO Recuperation le faite d'etre favori.
//                       Ajouter une methode de gestion et une mappage
                        List<FavoriConcert> myFavori = database.favoriDAO().loadAll();
                        isFavori = true; //myFavori.get(0).getIsFavori()==1? true : false;
/*                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(festival.toString());
                            }
                        });*/
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

    public void onResume() {
        super.onResume();


        adapter = new MyAdapterMainActivity(festival, this, isFavori);
        RecyclerView recyclerView = findViewById(R.id.groupes_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onClick(View view) {
        getDetaisl();
    }
}