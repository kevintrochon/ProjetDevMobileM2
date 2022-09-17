package nc.unc.ktrochon.festivalnotification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import nc.unc.ktrochon.festivalnotification.entity.DetailsDuConcert;

public class DescriptionDuConcertActivity extends AppCompatActivity {

    TextView textView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_du_concert);
        textView = (TextView) findViewById(R.id.ConcertDetails);
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
                        DetailsDuConcert detailConcert = genson.deserialize(scanner.nextLine(),DetailsDuConcert.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(detailConcert.toString());
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