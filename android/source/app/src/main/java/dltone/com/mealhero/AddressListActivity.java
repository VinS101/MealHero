package dltone.com.mealhero;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wgarcia on 11/18/2015.
 */
public class AddressListActivity extends AppCompatActivity
{
    //UI References
    private ListView mAddressListView;

    //List of Clients
    private AddressListAdapter addressAdapter;

    //App Reference
    MealHeroApplication MHApp;

    // Imported list
    List<Address> mAddressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        //Get reference to Meal Hero App
        MHApp = (MealHeroApplication) getApplication();

        //Get references to UI elements
        mAddressListView = (ListView) findViewById(R.id.clientListView);

        //Get list from previous activity
        mAddressList = getIntent().getParcelableArrayListExtra("Results_List");

        //Set up array adapter using search results
        addressAdapter = new AddressListAdapter(this, (ArrayList<Address>) mAddressList);
        mAddressListView.setAdapter(addressAdapter);

        //Notify adapter of data change
        addressAdapter.notifyDataSetChanged();

        //Set item click listener
        mAddressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent returnIntent = new Intent();
                returnIntent.putExtra("ItemLocation", (int) id);
                setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);
                finish();
                // set this as address
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_address_list, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back button
                Intent resultIntent = new Intent();
                setResult(MealHeroApplication.EDIT_ACTIVITY_RC, resultIntent);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
