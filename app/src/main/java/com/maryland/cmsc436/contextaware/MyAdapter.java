package com.maryland.cmsc436.contextaware;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.CompoundButton;

import org.w3c.dom.Text;

public class MyAdapter extends ArrayAdapter<ContextSettings> {

    Context context;
    ArrayList<ContextSettings> itemsArrayList;
    private static LayoutInflater inflater = null;


    public MyAdapter(Context context, ArrayList<ContextSettings> itemsArrayList) {
        super(context, R.layout.row, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return itemsArrayList.size();
    }

    @Override
    public ContextSettings getItem(int position) {
        // TODO Auto-generated method stub
        return itemsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void set(int position, ContextSettings object) {
        itemsArrayList.set(position, object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // First I will ge the context at the specified position
        final ContextSettings currentContext = (ContextSettings) getItem(position);

        View dataView = convertView;

        // check for recycled view
        if (dataView == null) {
            // not recycled, so we create the view here
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dataView = inflater.inflate(R.layout.row, parent, false);
        }

        TextView title = (TextView) dataView.findViewById(R.id.titleView);
        CheckBox check = (CheckBox) dataView.findViewById(R.id.statusCheckBox);
        TextView ringer = (TextView) dataView.findViewById(R.id.ringerView);
        title.setText(itemsArrayList.get(position).getTitle());
        if (itemsArrayList.get(position).getStatus().toString().equals("YES"))
            check.setChecked(true);
        else
            check.setChecked(false);
        ringer.setText(itemsArrayList.get(position).getRinger().toString());
        return dataView;
    }
}