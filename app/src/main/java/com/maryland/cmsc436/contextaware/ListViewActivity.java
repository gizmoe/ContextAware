package com.maryland.cmsc436.contextaware;

import android.app.Activity;
import android.nfc.Tag;
import android.widget.ListView;
import android.os.Bundle;
import android.widget.AdapterView;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Button;
import java.util.ArrayList;
import android.content.Intent;
import android.util.Log;

public class ListViewActivity extends Activity {
    EditText editText;
    Button addButton;
    ListView listView;
    ArrayList<ContextSettings> listItems;
    MyAdapter adapter;
    String title, status, ringer;
    ContextSettings.Ringer cRinger;
    ContextSettings.ActiveStatus cStatus;
    String cTitle;
    ContextSettings item;
    private static final int ADD_CONTEXT_REQUEST = 0;
    private static final String TAG = "ContextAware";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        addButton = (Button) findViewById(R.id.addItem);
        listView = (ListView) findViewById(R.id.listView);
        listView.setClickable(true);
        listItems = new ArrayList<ContextSettings>();
        listItems.add(new ContextSettings("First Item", ContextSettings.Ringer.SILENT, ContextSettings.ActiveStatus.YES));
        adapter = new MyAdapter(ListViewActivity.this, listItems);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent addNewItemIntent = new Intent(getApplicationContext(),AddNewItem.class);
                addNewItemIntent.putExtra("requestCode", 1);
                startActivityForResult(addNewItemIntent, 1);
                Toast.makeText(ListViewActivity.this, "Clicked", Toast.LENGTH_LONG)
                        .show();
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            //bring up the context edit menu
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {
                item = (ContextSettings) a.getItemAtPosition(position);
                cRinger = item.getRinger();
                cStatus = item.getStatus();
                cTitle = item.getTitle();

                Intent addNewItemIntent = new Intent(getApplicationContext(),AddNewItem.class);
                addNewItemIntent.putExtra("requestCode", 0);
                addNewItemIntent.putExtra("title", cTitle);
                addNewItemIntent.putExtra("status", cStatus.toString());
                addNewItemIntent.putExtra("ringer", cRinger.toString());
                addNewItemIntent.putExtra("pos", position);

                startActivityForResult(addNewItemIntent, ADD_CONTEXT_REQUEST);

                Toast.makeText(ListViewActivity.this, "Clicked", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK && requestCode==0){
            Bundle bundle = data.getExtras();
            title = bundle.getString("title");
            ringer = bundle.getString("ringer");
            status = bundle.getString("status");
            Integer pos = bundle.getInt("pos");

            item.setTitle(title);
            item.setRinger(ringer);
            item.setStatus(status);

            listItems.set(pos, item);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else if (resultCode==RESULT_OK && requestCode==1) {
            Bundle bundle = data.getExtras();
            title = bundle.getString("title");
            ringer = bundle.getString("ringer");
            status = bundle.getString("status");

            item = new ContextSettings(title, ContextSettings.Ringer.SILENT, ContextSettings.ActiveStatus.YES);
            item.setTitle(title);
            item.setRinger(ringer);
            item.setStatus(status);

            listItems.add(item);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
