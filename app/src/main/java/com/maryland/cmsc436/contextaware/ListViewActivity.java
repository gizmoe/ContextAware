package com.maryland.cmsc436.contextaware;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.view.Menu;
import android.view.MenuItem;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import android.content.Intent;
import android.util.Log;

public class ListViewActivity extends Activity {
    EditText editText;
    Button addButton;
    ListView listView;
    MyAdapter adapter;
    String title, status, ringer, location;
    ContextSettings.Ringer cRinger;
    ContextSettings.ActiveStatus cStatus;
    String cTitle;
    String cLocation;
    ContextSettings item;
    private static final int ADD_CONTEXT_REQUEST = 0;
    private static final String TAG = "ContextAware";
    // IDs for menu items
    private static final int MENU_DELETE = Menu.FIRST;
    private static final String FILE_NAME = "ContextActivity.txt";

    DBAccess db;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        addButton = (Button) findViewById(R.id.addItem);
        listView = (ListView) findViewById(R.id.listView);
        listView.setClickable(true);
        adapter = new MyAdapter(ListViewActivity.this, new ArrayList<ContextSettings>());
        listView.setAdapter(adapter);
        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent addNewItemIntent = new Intent(getApplicationContext(),AddNewItem.class);
                addNewItemIntent.putExtra("requestCode", 1);
                startActivityForResult(addNewItemIntent, 1);
                //Toast.makeText(ListViewActivity.this, "Clicked", Toast.LENGTH_LONG)
                  //      .show();
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
                cLocation = item.getLocation();

                Intent addNewItemIntent = new Intent(getApplicationContext(),AddNewItem.class);
                addNewItemIntent.putExtra("requestCode", 0);
                addNewItemIntent.putExtra("title", cTitle);
                addNewItemIntent.putExtra("location",cLocation);
                addNewItemIntent.putExtra("status", cStatus.toString());
                addNewItemIntent.putExtra("ringer", cRinger.toString());
                addNewItemIntent.putExtra("pos", position);

                startActivityForResult(addNewItemIntent, ADD_CONTEXT_REQUEST);

                //Toast.makeText(ListViewActivity.this, "Clicked", Toast.LENGTH_LONG)
                //        .show();
            }
        });

        // setting up the delete action when user clicks on list item for a long time
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                Log.i(TAG,"Entered the onItemLongClick method");

                AlertDialog.Builder alert = new AlertDialog.Builder(ListViewActivity.this);

                alert.setTitle("CAUTION");
                
                alert.setMessage("Do you want to delete this '" + adapter.getItem(pos).getTitle() + "' context?");


                // If the user clicks "Yes" on the dialog
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.removeSetting(adapter.remove(pos));
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                // If the user clicks "No" on the dialog
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                });

                // show the alertDialog
                alert.show();
               return true;
            }
        });

        db = DBAccess.getInstance(getApplicationContext());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK && requestCode==0){
            Bundle bundle = data.getExtras();
            title = bundle.getString("title");
            ringer = bundle.getString("ringer");
            status = bundle.getString("status");
            location = bundle.getString("location");
            Integer pos = bundle.getInt("pos");

            ContextSettings newItem = new ContextSettings(title,ringer,location,status);

            adapter.set(pos, newItem);
            db.updateSetting(newItem);
            adapter.notifyDataSetChanged();
        } else if (resultCode==RESULT_OK && requestCode==1) {
            Bundle bundle = data.getExtras();
            title = bundle.getString("title");
            ringer = bundle.getString("ringer");
            status = bundle.getString("status");
            location = bundle.getString("location");

            ContextSettings newItem = new ContextSettings(title,ringer,location,status);
            //Toast.makeText(getApplication(),ringer,Toast.LENGTH_LONG).show();

            // add this newly created context to the list
            adapter.add(newItem);
            db.putNewSetting(newItem);
            adapter.notifyDataSetChanged();
            // make a toast to indicate to the user that a context was successfully created
            Toast.makeText(getApplicationContext(),"Context Created",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        //should save here
        super.onResume();
        adapter.clear();
        adapter.addAll(db.getAllSettings());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"saved");
        //should load here


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
                for (ContextSettings c : adapter.itemsArrayList) {
                    db.removeSetting(c);
                }
                adapter.clear();
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
