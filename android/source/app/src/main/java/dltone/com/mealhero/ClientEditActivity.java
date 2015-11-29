package dltone.com.mealhero;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.mobile.services.data.IBMDataObject;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by costin on 10/22/2015.
 */
public class ClientEditActivity extends AppCompatActivity
{
    String CLASS_NAME = this.getClass().getName();

    //Local copy of Client
    Client client;

    //Original Copy of Client
    Client originalClient;

    //App reference
    MealHeroApplication MHApp;

    View clientEditView;
    View progress;

    //UI Elements
    ImageView userImage;
    EditText nameTextBox;
    EditText addressTextBox;
    EditText ageTextBox;
    EditText dietTextBox;
    TextView assignedToTextView;

    //Background Thread
    private DeleteClient mAuthTask = null;

    //misc
    private boolean isDeleted = false;
    private boolean isEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);

        //Get references to UI elements
        userImage = (ImageView) findViewById(R.id.client_edit_image);
        nameTextBox = (EditText) findViewById(R.id.client_edit_name);
        addressTextBox = (EditText) findViewById(R.id.client_edit_address_box);
        ageTextBox = (EditText) findViewById(R.id.client_edit_age_box);
        dietTextBox = (EditText) findViewById(R.id.client_edit_diet_box);
        assignedToTextView = (TextView) findViewById(R.id.client_edit_assigned_to);

        //Get App Reference
        MHApp = (MealHeroApplication) getApplication();
        clientEditView = findViewById(R.id.editClient_form);
        progress = findViewById(R.id.editClient_progress);
        //Get Selected Client
        int index = getIntent().getIntExtra("ItemLocation", -1);
        if(index >= 0) {
            client = MHApp.getClientList().get(index);
            originalClient = new Client(client.getName(), client.getAddress(), client.getDiet(), client.getAge(), client.getAssigned(), client.getAssignedTo(), client.getLatitude(), client.getLongitude());
        } else {
            client = null;
            originalClient = null;
        }

        //Set UI values
        nameTextBox.setText(client.getName());
        addressTextBox.setText(client.getAddress());
        ageTextBox.setText(client.getAge());
        dietTextBox.setText(client.getDiet());
        assignedToTextView.setText(client.getAssignedTo());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (client != null) {
                    client.setName(nameTextBox.getText().toString());
                    //TODO: client.setAddress(addressTextBox.getText().toString());
                    client.setDietPreference(dietTextBox.getText().toString());
                    client.setAge(ageTextBox.getText().toString());
                    if(!client.equals(originalClient)) {
                        client.save().continueWith(new Continuation<IBMDataObject, Void>() {
                            @Override
                            public Void then(Task<IBMDataObject> task) throws Exception {
                                if (task.isCancelled()) {
                                    Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                                } else if (task.isFaulted()) {
                                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                                } else {

                                }
                                return null;
                            }
                        });
                        Toast.makeText(MHApp.getApplicationContext(), "Client changes saved!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MHApp.getApplicationContext(), "Error! Could not save changes!", Toast.LENGTH_LONG).show();
                }
                Intent returnIntent = new Intent();
                setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);
                finish();
                return true;
            case R.id.menu_delete_client:
                showProgress(true);
                mAuthTask = new DeleteClient();
                mAuthTask.execute((Void) null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);
            int longAnimationTime = 1000; //need longer
            clientEditView.setVisibility(show ? View.GONE : View.VISIBLE);
            clientEditView.animate().setDuration(longAnimationTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation) {
                    clientEditView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            clientEditView.setVisibility(show ? View.VISIBLE : View.GONE);
            clientEditView.animate().setDuration(longAnimationTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    clientEditView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progress.setVisibility(show ? View.VISIBLE : View.GONE);
            clientEditView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public class DeleteClient extends AsyncTask<Void, Void, Boolean>
    {


        DeleteClient()
        {

        }

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Boolean doInBackground(Void... params)
        {

            if(client != null)
            {
                MHApp.getClientList().remove(client);
                client.delete().continueWith(new Continuation<IBMDataObject, Void>() {
                    @Override
                    public Void then(Task<IBMDataObject> task) throws Exception {
                        if (task.isCancelled())
                        {
                            Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                        } else if (task.isFaulted())
                        {
                            Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                        } else
                        {

                        }
                        return null;
                    }
                });
                isDeleted = true;
            } else
            {
                Toast.makeText(MHApp.getApplicationContext(), "Error! Could not delete client!", Toast.LENGTH_LONG).show();
            }
            Intent returnIntent = new Intent();
            setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);
            finish();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            mAuthTask = null;
            showProgress(false);
            if(isDeleted)
            {
                Toast.makeText(MHApp.getApplicationContext(), "Client deleted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(MHApp.getApplicationContext(), "Error! Could not delete client!", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled()
        {
            mAuthTask = null;
            //showProgress(false);
        }
    }
}
