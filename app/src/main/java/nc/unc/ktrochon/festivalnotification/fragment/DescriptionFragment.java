package nc.unc.ktrochon.festivalnotification.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import nc.unc.ktrochon.festivalnotification.DescriptionDuConcertActivity;
import nc.unc.ktrochon.festivalnotification.R;

/**
 * Fragment qui permet l affichage de la description du groupe / artiste.
 */
public class DescriptionFragment extends Fragment {

    private DescriptionDuConcertActivity descriptionDuConcertActivity;

    private static final String ARG_PARAM1 = "param1";

    private String description;

    public DescriptionFragment() {
    }

    public static DescriptionFragment newInstance(String param1) {
        DescriptionFragment fragment = new DescriptionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            description = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_description, container, false);
        TextView textView = (TextView)  view.findViewById(R.id.Concert_Description);
        textView.setText(description);
        return view;
    }

}