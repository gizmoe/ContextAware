package com.maryland.cmsc436.contextaware;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ListActivity {
    private static final String TAG = "ContextAware";
    private static final int ADD_CONTEXT_REQUEST = 0;

    ContextListAdapter contextAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Put divider between contexts and FooterView
        getListView().setFooterDividersEnabled(true);

        // make it TextView since that is what footer_view is in the XML
        // pass in null for the view root for now
        TextView footerView = (TextView) getLayoutInflater().inflate(R.layout.footer_view,null);

        // here I will add the footerView to the ListView using the addFooterView method
        this.getListView().addFooterView(footerView);

        // Here we will attach a Listener to the FooterView
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "entered the onClickListener for FooterView");

				/*
				 Here I will create an intent so that I can start my AddNewContext activity if the
				 FooterView is clicked.
				  */
                Intent addNewContextIntent = new Intent(getApplicationContext(),AddNewContext.class);
				/*
				 I will use StartActivityForResult rather than startActivity so that I can pass it
				 in the proper requestCode.
				  */
                startActivityForResult(addNewContextIntent, ADD_CONTEXT_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "Entered onActivityResult()");

        // Here I will check that the requestCode and the resultCode are what they should be
        if (requestCode == ADD_CONTEXT_REQUEST && resultCode == RESULT_OK) {

            // Create the context from the data Intent
            ContextSettings newContext = new ContextSettings(data);

            // Here I will add it to the adapter
            contextAdapter.add(newContext);
        }
    }
}
