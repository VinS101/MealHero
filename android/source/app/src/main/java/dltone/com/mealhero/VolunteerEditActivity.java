package dltone.com.mealhero;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * costin 11/6/2015
 */

public class VolunteerEditActivity extends AppCompatActivity
{
    String CLASS_NAME = this.getClass().getName();

    //Volunteer reference
    Volunteer volunteer;
    //Original Volunteer
    Volunteer originalVolunteer;

    //UI Elements
    EditText nameTextBox;
    View volunteerEdit;
    View progress;
    EditText passwordTextBox;
    EditText emailTextBox;
    EditText permissionTextBox;
    Button assignClientsButton;

    //App reference
    MealHeroApplication MHApp;

    private DeleteVolunteer mAuthTask = null;
    private boolean isDeleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_volunteer);

        //Get references to UI elements
        nameTextBox = (EditText) findViewById(R.id.volunteer_edit_name_box);
        emailTextBox = (EditText) findViewById(R.id.volunteer_edit_email_box);
        passwordTextBox = (EditText) findViewById(R.id.volunteer_edit_password_box);
        passwordTextBox.setEnabled(false);
        permissionTextBox = (EditText) findViewById(R.id.volunteer_edit_permission_box);
        assignClientsButton = (Button) findViewById(R.id.volunteer_edit_button);
        volunteerEdit = findViewById(R.id.editVolunteer_form);
        progress = findViewById(R.id.editVolunteer_progress);
        //Get App Reference
        MHApp = (MealHeroApplication) getApplication();

        //Always Get Volunteers
        if(MHApp.getVolunteerList().size() < 1) {
            MHApp.setVolunteerList(VolunteerProvider.GetVolunteers());
        }

        //Get Volunteer Reference
        final int index = getIntent().getIntExtra("ItemLocation", 0);
        if (MHApp.getVolunteerList().size() > 0) {
            volunteer = MHApp.getVolunteerList().get(index);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "An error occurred retrieving the volunteer. Please contact an administrator.", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);
            finish();
        }

        //Set UI Values
        if (volunteer != null) {
            nameTextBox.setText(volunteer.getName());
            passwordTextBox.setText(volunteer.getPassword());
            emailTextBox.setText(volunteer.getEmail());
            permissionTextBox.setText(volunteer.getPermission());
        }

        //Initialize original Volunteer
        if(volunteer != null) {
            originalVolunteer = new Volunteer(volunteer.getName(), volunteer.getPassword(), volunteer.getEmail(), volunteer.getPermission(), volunteer.getClientIdsList());
        }
        else {
            originalVolunteer = null;
        }

        //Assign Clients Button Behavior
        assignClientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent assignClientsIntent = new Intent(getApplicationContext(), AssignClientsActivity.class);
                assignClientsIntent.putExtra("ItemLocation", index);
                startActivity(assignClientsIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_volunteer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                if (volunteer != null) {
                    saveVolunteer(volunteer);
                    if(!volunteer.equals(originalVolunteer)){
                        Toast.makeText(getApplicationContext(), "Volunteer edit successful!", Toast.LENGTH_SHORT).show();
                        VolunteerProvider.SaveVolunteer(volunteer);
                    }
                } else {
                    Toast.makeText(MHApp.getApplicationContext(), "Error! Could not save changes!", Toast.LENGTH_LONG).show();
                }
                Intent returnIntent = new Intent();
                setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);
                finish();
                return true;
            case R.id.menu_delete_volunteer:
                //Run in background
                showProgress(true);
                mAuthTask = new DeleteVolunteer();
                mAuthTask.execute((Void) null);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);
            int longAnimationTime = 1000; //need longer
            volunteerEdit.setVisibility(show ? View.GONE : View.VISIBLE);
            volunteerEdit.animate().setDuration(longAnimationTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation) {
                    volunteerEdit.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            volunteerEdit.setVisibility(show ? View.VISIBLE : View.GONE);
            volunteerEdit.animate().setDuration(longAnimationTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    volunteerEdit.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progress.setVisibility(show ? View.VISIBLE : View.GONE);
            volunteerEdit.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void saveVolunteer(Volunteer v) {

        v.setName(nameTextBox.getText().toString());
        v.setEmail(emailTextBox.getText().toString());
        v.setPermission(permissionTextBox.getText().toString());
        //v.setClientList();
    }
    public class DeleteVolunteer extends AsyncTask<Void, Void, Boolean>
    {


        DeleteVolunteer()
        {

        }

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            //Give user the illusion of progress
            try
            {
                Thread.sleep(500);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            Intent returnIntent = new Intent();
            setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);

            if (volunteer == null)
            {
                Toast.makeText(MHApp.getApplicationContext(), "Error! Could not delete Volunteer!", Toast.LENGTH_LONG).show();
                finish();
                return true;
            }

            if (volunteer.getEmail().equalsIgnoreCase(MHApp.getLoggedInVolunteer().getEmail()))
            {
                Toast.makeText(getApplicationContext(), "You cannot delete your own account!", Toast.LENGTH_SHORT).show();
                return true;
            }
            else
            {
                ArrayList<Client> assignedClients = ClientProvider.GetAssignedClients(volunteer, MHApp);
                for (Client c : assignedClients)
                {
                    c.setAssigned(false);
                    ClientProvider.SaveClient(c);
                }

                VolunteerProvider.DeleteVolunteer(volunteer);
                isDeleted = true;
                //MHApp.getVolunteerList().remove(volunteer);
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                MHApp.setVolunteerList(VolunteerProvider.GetVolunteers());
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            mAuthTask = null;
            showProgress(false);
            Intent intent = new Intent(VolunteerEditActivity.this, AdministrationActivity.class);
            startActivity(intent);
            if(isDeleted)
            {
                Toast.makeText(MHApp.getApplicationContext(), "Volunteer was successfully deleted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(MHApp.getApplicationContext(), "Error! Could not delete volunteer!", Toast.LENGTH_LONG).show();
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
