package nc.unc.ktrochon.festivalnotification.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nc.unc.ktrochon.festivalnotification.R;

public class MySpinnerAdapter extends BaseAdapter {

    List<String> monFiltre = new ArrayList<>();
    Activity activity;
    LayoutInflater inflater;
    int itemSpinnerID;
    int textViewID;

    public MySpinnerAdapter(Activity monActivity,List<String> filtre,int itemSpinnerID, int textViewID) {
        activity = monActivity;
        inflater = monActivity.getLayoutInflater();
        this.monFiltre = filtre;
        this.itemSpinnerID = itemSpinnerID;
        this.textViewID = textViewID;
    }

    @Override
    public int getCount() {
        return monFiltre.size();
    }

    @Override
    public Object getItem(int i) {
        return monFiltre.get(i);
    }

    @Override
    public long getItemId(int i) {
        return monFiltre.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        String choix = monFiltre.get(position);

        View rowView = this.inflater.inflate(itemSpinnerID,null,true);
        TextView textView = rowView.findViewById(textViewID);
        textView.setText(choix);

        return rowView;
    }
}
