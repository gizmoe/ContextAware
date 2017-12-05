package com.maryland.cmsc436.contextaware;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.CompoundButton;

public class MyAdapter extends ArrayAdapter<ContextSettings> {

    private final Context context;
    private final ArrayList<ContextSettings> itemsArrayList;
    private final DBAccess db;

    public MyAdapter(Context context, ArrayList<ContextSettings> itemsArrayList) {

        super(context, R.layout.row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
        db = DBAccess.getInstance(context);
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // First I will ge the context at the specified position
        final ContextSettings currentContext = (ContextSettings) getItem(position);

        View dataView = convertView;
        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // check for recycled view
        if (null == dataView) {
            // not recycled, so we create the view here
            dataView = mLayoutInflater.inflate(R.layout.row,parent,false);

            // Create a viewHolder so that I can use the viewHolder pattern for easier scrolling
            ViewHolder myViewHolder = new ViewHolder();

            myViewHolder.ringerView = (TextView) dataView.findViewById(R.id.RingerLabel);

            myViewHolder.titleView = (TextView) dataView.findViewById(R.id.titleView);

            myViewHolder.locationView = (TextView) dataView.findViewById(R.id.locationView);

            myViewHolder.statusView = (CheckBox) dataView.findViewById(R.id.statusCheckBox);

            myViewHolder.position = position;
            myViewHolder.contextLayout = (RelativeLayout) dataView;
            dataView.setTag(myViewHolder);
        }

        ViewHolder storedViewHolder = (ViewHolder) dataView.getTag();
        // set the data in the data View

        storedViewHolder.ringerView.setText("Ringer setting: " + currentContext.getRinger().toString());
        storedViewHolder.titleView.setText(currentContext.getTitle());
        storedViewHolder.locationView.setText("Location: " + currentContext.getLocation());
        storedViewHolder.statusView.setChecked(currentContext.getStatus() == ContextSettings.ActiveStatus.YES);
        storedViewHolder.statusView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                // If it is checked, set the status to Active (or YES), otherwise set it to NO
                if (b == true) {
                    currentContext.setStatus("yes");
                    db.updateSetting(currentContext);

                } else {
                    currentContext.setStatus("no");
                    db.updateSetting(currentContext);
                }
            }
        });
        return dataView;
    }

    static class ViewHolder {
        int position;
        RelativeLayout contextLayout;
        TextView titleView;
        CheckBox statusView;
        TextView ringerView;
        TextView locationView;
    }
}