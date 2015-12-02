package dltone.com.mealhero;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class VolunteerListActivity extends AppCompatActivity
{
    private ArrayAdapter<Volunteer> lvArrayAdapter;
    MealHeroApplication MHA;
    private SetupTask setupTask = null;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Leave UI thread alone
        SetupTask setup = new SetupTask();
        setup.execute((Void) null);
        CountDownLatch latch = new CountDownLatch(1);

        setContentView(R.layout.activity_volunteer_list);

        /* Use application class to maintain global state. */
        MHA = (MealHeroApplication) getApplication();
        //if(MHA.getVolunteerList().size() == 0) {
            MHA.setVolunteerList(VolunteerProvider.GetVolunteers());
        //}

        ListView volunteerListView = (ListView) findViewById(R.id.VolunteerListView);
        lvArrayAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, MHA.getVolunteerList());
        volunteerListView.setAdapter(lvArrayAdapter);

        lvArrayAdapter.notifyDataSetChanged();
        /* Set up the array adapter for items list view. */

        //Get role from Intent
        role = getIntent().getStringExtra("Role");

        //Set item click listener
        volunteerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                if(role.equals("EditVolunteer")) {
                    intent = new Intent(getApplicationContext(), VolunteerEditActivity.class);
                } else if(role.equals("AssignClients")) {
                    intent = new Intent(getApplicationContext(), AssignClientsActivity.class);
                } else {
                    Log.e(getClass().toString(), "Unknown role '" + role + "'!!!");
                }

                if(intent != null) {
                    intent.putExtra("ItemLocation", (int) id);
                    startActivityForResult(intent, MealHeroApplication.EDIT_ACTIVITY_RC);
                }
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
                lvArrayAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_volunteer_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SetupTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params)
        {

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            setupTask = null;
        }

        @Override
        protected void onCancelled()
        {
            setupTask = null;
        }
    }
}
