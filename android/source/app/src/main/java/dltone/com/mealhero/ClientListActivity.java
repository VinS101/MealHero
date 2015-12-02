package dltone.com.mealhero;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by costin on 10/6/2015.
 */
public class ClientListActivity extends AppCompatActivity
{
    //UI References
    private ListView mClientListView;

    //List of Clients
    private ClientListAdapter clientAdapter;

    //App Reference
    MealHeroApplication MHApp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        //Get reference to Meal Hero App
        MHApp = (MealHeroApplication) getApplication();

        //Get references to UI elements
        mClientListView = (ListView) findViewById(R.id.clientListView);

        //Ensure Client List is retrieved from DB
        if(MHApp.getClientList().size() < 1) {
            MHApp.setClientList(ClientProvider.GetClients());

        }
        Collections.sort(MHApp.getClientList(), new Comparator<Client>()
        {
            @Override
            public int compare(Client c1, Client c2)
            {
                return c1.getName().toUpperCase().compareTo(c2.getName().toUpperCase());
            }
        });

        //Set up array adapter
        clientAdapter = new ClientListAdapter(this, (ArrayList<Client>) MHApp.getClientList());
        mClientListView.setAdapter(clientAdapter);

        //Notify adapter of data change
        clientAdapter.notifyDataSetChanged();

        //Set item click listener
        mClientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ClientEditActivity.class);
                intent.putExtra("ItemLocation", (int) id);
                startActivityForResult(intent, MealHeroApplication.EDIT_ACTIVITY_RC);
            }
        });
    }

    /**
     * On return from other activity, check result code to determine behavior.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (resultCode)
        {
		/* If an edit has been made, notify that the data set has changed. */
            case MealHeroApplication.EDIT_ACTIVITY_RC:
                clientAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client_list, menu);
        return true;
    }
}
