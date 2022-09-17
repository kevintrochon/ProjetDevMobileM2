package nc.unc.ktrochon.festivalnotification;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import nc.unc.ktrochon.festivalnotification.entity.ListeDesConcerts;
import nc.unc.ktrochon.festivalnotification.notification.ConcertNotification;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button detailButton;
    private Button notification;
    private Button denotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        textView = (TextView) findViewById(R.id.text_API);
        detailButton = (Button) findViewById(R.id.DetailsButton);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDetaisl();
            }
        });

        //TODO A supprimer.
        ConcertNotification concertNotification = new ConcertNotification(MainActivity.this);
        notification = (Button) findViewById(R.id.NotificationButton);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                concertNotification.notify(1,false, "My Notification"," HelloWorld");
            }
        });

        denotification = (Button) findViewById(R.id.DenotificationButton);
        denotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                concertNotification.cancelNotification(1);
            }
        });
    }

    private void getDetaisl() {
        Intent intent = new Intent(this, DescriptionDuConcertActivity.class);
        this.startActivity(intent);
    }

    public void onResume() {
        super.onResume();
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
                        ListeDesConcerts festival = genson.deserialize(scanner.nextLine(),ListeDesConcerts.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(festival.toString());
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