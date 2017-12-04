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
    ArrayList<ContextSettings> listItems;
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
        listItems = new ArrayList<ContextSettings>();
        //listItems.add(new ContextSettings("First Item", ContextSettings.Ringer.SILENT, ContextSettings.ActiveStatus.YES));
        adapter = new MyAdapter(ListViewActivity.this, listItems);
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
                alert.setMessage("Are you sure you want to delete this context?");

                // If the user clicks "Yes" on the dialog
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.removeSetting(listItems.remove(pos));
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

            item.setTitle(title);
            item.setRinger(ringer);
            item.setStatus(status);
            item.setLocation(location);

            listItems.set(pos, item);
            db.updateSetting(item);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else if (resultCode==RESULT_OK && requestCode==1) {
            Bundle bundle = data.getExtras();
            title = bundle.getString("title");
            ringer = bundle.getString("ringer");
            status = bundle.getString("status");
            location = bundle.getString("location");

            item = new ContextSettings(title, ContextSettings.Ringer.SILENT, location, ContextSettings.ActiveStatus.YES);
            item.setTitle(title);
            item.setRinger(ringer);
            item.setStatus(status);
            item.setLocation(location);

            // add this newly created context to the list
            //listItems.add(item);
            db.putNewSetting(item);

            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // make a toast to indicate to the user that a context was successfully created
            Toast.makeText(getApplicationContext(),"Context Created",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load saved ToDoItems, if necessary
//        if (listItems.size() == 0) {
//            loadItems();
//        }
        listItems.clear();
        listItems.addAll(db.getAllSettings());

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save Contexts
//        saveItems();
        //db should be saving things as it gets them

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
                for (ContextSettings c : listItems) {
                    db.removeSetting(c);
                }
                listItems.clear();
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Load stored Contexts
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
                //Toast.makeText(getApplicationContext(),location,Toast.LENGTH_LONG).show();
                //ContextSettings newItem = reader.readLine();
                listItems.add(new ContextSettings(title, ContextSettings.Ringer.valueOf(ringer),
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

    // Save Contexts to file
    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for (int idx = 0; idx < listItems.size(); idx++) {

                writer.println(listItems.get(idx));

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
