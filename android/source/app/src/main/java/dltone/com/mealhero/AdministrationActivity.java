package dltone.com.mealhero;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.os.AsyncTask;
public class AdministrationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    private VolunteerListTask mAuthTask = null;
    private View adminList;
    private View progressView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administration);
        ListView optionsView = (ListView) findViewById(R.id._lvwAdminOptions);
        adminList = findViewById(R.id._lvwAdminOptions);
        progressView = findViewById(R.id.Admin_progress);
        optionsView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_administration, menu);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent = new Intent();
        switch(position)
        {
            case 0:
                intent = new Intent(AdministrationActivity.this, AddVolunteerActivity.class);
                startActivity(intent);
                break;
            case 1:
                showProgress(true);
                mAuthTask = new VolunteerListTask("EditVolunteer");
                mAuthTask.execute((Void) null);
                break;
            case 2:
                intent = new Intent(AdministrationActivity.this, AddClientActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(AdministrationActivity.this, ClientListActivity.class);
                startActivity(intent);
                break;
            case 4:
                showProgress(true);
                mAuthTask = new VolunteerListTask("AssignClients");
                mAuthTask.execute((Void) null);
                break;
            case 5:
                intent = new Intent(AdministrationActivity.this, MapPreviewActivity.class);
                startActivity(intent);
                break;
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            adminList.setVisibility(show ? View.GONE : View.VISIBLE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            adminList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class VolunteerListTask extends AsyncTask<Void, Void, Boolean>
    {
        VolunteerListTask(String role)
        {
            this.role = role;
        }
        Intent intent;
        @Override
        protected Boolean doInBackground(Void... params)
        {
            intent = new Intent(AdministrationActivity.this, VolunteerListActivity.class);
            intent.putExtra("Role", role);
            startActivity(intent);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            mAuthTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled()
        {
            mAuthTask = null;
            showProgress(false);
        }

        private String role;
    }
}
