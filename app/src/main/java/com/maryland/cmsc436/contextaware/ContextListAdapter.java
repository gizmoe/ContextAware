package com.maryland.cmsc436.contextaware;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ContextListAdapter extends BaseAdapter {
    private final List<ContextSettings> contexts = new ArrayList<ContextSettings>();
    private final Context mContext;

    private static final String TAG = "Lab-UserInterface";

    public ContextListAdapter(Context context) {
        mContext = context;
    }

    // Add a new context to the adapter
    // Notify observers that the data set has changed
    public void add(ContextSettings newContext) {
        contexts.add(newContext);
        notifyDataSetChanged();
    }

    // Clears the list adapter of all contexts.
    public void clear() {
        contexts.clear();
        notifyDataSetChanged();
    }

    // Returns the number of contexts
    @Override
    public int getCount() {
        return contexts.size();
    }

    // Retrieve the number of contexts
    @Override
    public Object getItem(int pos) {
        return contexts.get(pos);
    }

    // Get the ID for the context
    // In this case it's just the position
    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // First I will ge the context at the specified position
        final ContextSettings currentContext = (ContextSettings) getItem(position);

        View dataView = convertView;
        LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // check for recycled view
        if (null == dataView) {
            // not recycled, so we create the view here
            dataView = mLayoutInflater.inflate(R.layout.activity_main,parent,false);

            // Create a viewHolder so that I can use the viewHolder pattern for easier scrolling
            ViewHolder myViewHolder = new ViewHolder();

            myViewHolder.ringerView = (TextView) dataView.findViewById(R.id.RingerLabel);

            myViewHolder.titleView = (TextView) dataView.findViewById(R.id.titleView);

            myViewHolder.statusView = (CheckBox) dataView.findViewById(R.id.statusCheckBox);

            myViewHolder.position = position;
            myViewHolder.contextLayout = (RelativeLayout) dataView;
            dataView.setTag(myViewHolder);
        }

        ViewHolder storedViewHolder = (ViewHolder) dataView.getTag();
        // set the data in the data View

        storedViewHolder.ringerView.setText("Ringer setting: " + currentContext.getRinger().toString());
        storedViewHolder.titleView.setText(currentContext.getTitle());
        storedViewHolder.statusView.setChecked(currentContext.getStatus() == ContextSettings.ActiveStatus.YES);
        storedViewHolder.statusView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i(TAG, "Entered the checker to make sure the checkbox is set properly");

                // If it is checked, set the status to Active (or YES), otherwise set it to NO
                if (b == true) {
                    currentContext.setStatus(ContextSettings.ActiveStatus.YES);
                } else {
                    currentContext.setStatus(ContextSettings.ActiveStatus.NO);
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
