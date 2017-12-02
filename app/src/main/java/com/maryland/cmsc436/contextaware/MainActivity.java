package com.maryland.cmsc436.contextaware;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class MainActivity extends ListActivity {
    private static final String TAG = "ContextAware";
    private static final int ADD_CONTEXT_REQUEST = 0;

    // IDs for menu items
    private static final int MENU_DELETE = Menu.FIRST;
    private static final int MENU_DUMP = Menu.FIRST + 1;
    private static final String FILE_NAME = "ContextActivity.txt";

    ContextListAdapter contextAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextAdapter = new ContextListAdapter(getApplicationContext());

        ListView listView = getListView();
        registerForContextMenu(listView);
        // Put divider between contexts and FooterView
        listView.setFooterDividersEnabled(true);

        // make it TextView since that is what footer_view is in the XML
        // pass in null for the view root for now
        final View footerView = (TextView) getLayoutInflater().inflate(R.layout.footer_view,null);
        // here I will add the footerView to the ListView using the addFooterView method
        listView.addFooterView(footerView);

        // Here we will attach a Listener to the FooterView
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "entered the onClickListener for FooterView");
				 //Here I will create an intent so that I can start my AddNewContext activity if the
				 //FooterView is clicked.

                Intent addNewContextIntent = new Intent(getApplicationContext(),AddNewContext.class);

				 //I will use StartActivityForResult rather than startActivity so that I can pass it
				 //in the proper requestCode.

                startActivityForResult(addNewContextIntent, ADD_CONTEXT_REQUEST);
            }
        });
        // This will attach the adapter to this ListActivity's ListView
        setListAdapter(contextAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "Entered onActivityResult()");

        // Here I will check that the requestCode and the resultCode are what they should be
        if (requestCode == ADD_CONTEXT_REQUEST && resultCode == RESULT_OK) {

            // make a toast message indicating the context was successfully created
            Toast.makeText(getApplicationContext(),"Context Created",Toast.LENGTH_LONG).show();

            // Create the context from the data Intent
            ContextSettings newContext = new ContextSettings(data);

            // Here I will add it to the adapter
            contextAdapter.add(newContext);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load saved ToDoItems, if necessary

        if (contextAdapter.getCount() == 0)
            loadItems();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save ToDoItems

        saveItems();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete all contexts");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DELETE:
                contextAdapter.clear();
                return true;
            case MENU_DUMP:
                dump();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void dump() {
        for (int i = 0; i < contextAdapter.getCount(); i++) {
            String data = ((ContextSettings) contextAdapter.getItem(i)).toLog();
            Log.i(TAG,
                    "Item " + i + ": " + data.replace(ContextSettings.ITEM_SEP, ","));
        }
    }

    // Load stored ToDoItems
    private void loadItems() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String title = null;
            String ringer = null;
            String location = null;
            String status = null;

            while (null != (title = reader.readLine())) {
                ringer = reader.readLine();
                status = reader.readLine();
                location = reader.readLine();
                contextAdapter.add(new ContextSettings(title, ContextSettings.Ringer.valueOf(ringer),
                        location, ContextSettings.ActiveStatus.valueOf(status)));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Save ToDoItems to file
    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for (int idx = 0; idx < contextAdapter.getCount(); idx++) {

                writer.println(contextAdapter.getItem(idx));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }
}
