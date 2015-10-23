package dltone.com.mealhero;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

/**
 * Created by costin on 10/6/2015.
 */
public class ClientListActivity extends AppCompatActivity
{
    //UI References
    private ListView mClientListView;

    //List of Clients
    private ArrayList<Client> clients;
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

        //Get list of Clients
        clients = (ArrayList) MHApp.getClientList();

        //Set up array adapter
        clientAdapter = new ClientListAdapter(this, clients);
        mClientListView.setAdapter(clientAdapter);

        //Notify adapter of data change
        clientAdapter.notifyDataSetChanged();

        final Context context = this;
        //Set item click listener
        mClientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ClientEditActivity.class);
                Client client = clients.get((int) id);
                intent.putExtra("CLIENT", client.toHashMap());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client_list, menu);
        return true;
    }
}
