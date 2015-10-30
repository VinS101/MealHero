package dltone.com.mealhero;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DisplayVolunteerViewActivity extends AppCompatActivity
{
    private ArrayAdapter<Volunteer> lvArrayAdapter;
    private List<Volunteer> query;
    MealHeroApplication MHA;
    private SetupTask setupTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Leave UI thread alone
        SetupTask setup = new SetupTask();
        setup.execute((Void) null);
        CountDownLatch latch = new CountDownLatch(1);

        setContentView(R.layout.activity_display_volunteer_view);
            /* Use application class to maintain global state. */
        MHA = (MealHeroApplication) getApplication();
        query = VolunteerProvider.GetVolunteers();

        ListView volunteerListView = (ListView) findViewById(R.id._lvwVolunteerList);
        lvArrayAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, query);
        volunteerListView.setAdapter(lvArrayAdapter);
        //query = VolunteerProvider.GetVolunteers();

        lvArrayAdapter.notifyDataSetChanged();
        /* Set up the array adapter for items list view. */


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
