package com.maryland.cmsc436.contextaware;

import android.app.Activity;
import android.widget.ListView;
import android.os.Bundle;
import android.widget.AdapterView;
import android.view.View;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Button;
import java.util.ArrayList;

public class ListViewActivity extends Activity {
    EditText editText;
    Button addButton;
    ListView listView;
    ArrayList<Item> listItems;
    MyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        addButton = (Button) findViewById(R.id.addItem);
        listView = (ListView) findViewById(R.id.listView);
        listView.setClickable(true);
        listItems = new ArrayList<Item>();
        listItems.add(new Item("First Item", ContextSettings.Ringer.SILENT, ContextSettings.ActiveStatus.YES));
        adapter = new MyAdapter(ListViewActivity.this, listItems);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(ListViewActivity.this, "Clicked", Toast.LENGTH_LONG)
                        .show();
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            //bring up the context edit menu
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {
                Toast.makeText(ListViewActivity.this, "Clicked", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
