package nc.unc.ktrochon.festivalnotification.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import nc.unc.ktrochon.festivalnotification.R;

/**
 * Fragment pour l affichage de la photo du groupe ou de l artiste.
 */
public class ImageFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;
    private static final String API_URL = "https://daviddurand.info/D228/festival/";

    public ImageFragment() {
    }

    public static ImageFragment newInstance(String param1) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        HttpsURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(API_URL + mParam1);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            inputStream = new BufferedInputStream(connection.getInputStream());
            Scanner scanner = new Scanner(inputStream);
            Genson genson = new GensonBuilder().useConstructorWithArguments(true).create();
            connection = (HttpsURLConnection) url.openConnection();

            if (connection.getResponseCode()== HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ImageView imageView = view.findViewById(R.id.Concert_Image);
                imageView.setImageBitmap(bitmap);
            }
        }catch (Exception e){
            Log.e("Exchange-JSON :","Connot found the API", e);
        }
        finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}