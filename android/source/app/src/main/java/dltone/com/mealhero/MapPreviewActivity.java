package dltone.com.mealhero;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MapPreviewActivity extends AppCompatActivity
{
    Volunteer mVolunteerToDisplay;
    ArrayAdapter<Client> lvArrayAdapter;
    List<Client> query = new ArrayList<>();
    MealHeroApplication MHApp;

    private static final int MENU_ADMIN = Menu.FIRST;
    private static final int MENU_SETTINGS = Menu.FIRST + 1;
    private static final int MENU_LOGOUT = Menu.FIRST + 2;
    private Button _btnNavigate;

    public final static String VOLUNTEER = "dltone.com.mealhero.VOLUNTEER";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_preview);
        Bundle incoming = getIntent().getExtras();

        if (incoming != null)
        {
            mVolunteerToDisplay = (Volunteer)incoming.getSerializable(VOLUNTEER);
        }

        /* Use application class to maintain global state. */
        MHApp = (MealHeroApplication) getApplication();
        query = MHApp.getClientList();

        /* Set up the array adapter for items list view. */
        ListView volunteerListView = (ListView) findViewById(R.id._lvwClients);
        lvArrayAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, query);
        volunteerListView.setAdapter(lvArrayAdapter);

        //query = ClientProvider.GetClients();

        lvArrayAdapter.notifyDataSetChanged();

        _btnNavigate = (Button) findViewById(R.id._btnNavigate);
        _btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openNavigation();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_preview, menu);

        return true;
    }


    /**
     * Gets called every time the user presses the menu button.
     * Use if your menu is dynamic.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        //if (mVolunteerToDisplay.getPermission().equalsIgnoreCase("Admin"))
        menu.add(0, MENU_ADMIN, Menu.NONE, R.string.action_admin);

        menu.add(0, MENU_SETTINGS, Menu.NONE, R.string.action_settings);
        menu.add(0, MENU_LOGOUT, Menu.NONE, R.string.action_logout);

        getMenuInflater().inflate(R.menu.menu_map_preview, menu);

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case MENU_ADMIN:
                openAdministration();
                break;
            case MENU_SETTINGS:
                break;
            case MENU_LOGOUT:
                logout();
                break;
            case R.id.action_refresh:
                refresh();
                break;
        }
        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings)
        //{
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    private void openNavigation()
    {
        Intent intent = new Intent(MapPreviewActivity.this, NavigationActivity.class);
        startActivity(intent);
    }

    private void openAdministration()
    {
        Intent intent = new Intent(MapPreviewActivity.this, AdministrationActivity.class);
        startActivity(intent);
    }

    private void logout()
    {
        Intent intent = new Intent(MapPreviewActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void refresh()
    {
        finish();
        Intent intent = new Intent(MapPreviewActivity.this, MapPreviewActivity.class);
        startActivity(intent);
    }
}
