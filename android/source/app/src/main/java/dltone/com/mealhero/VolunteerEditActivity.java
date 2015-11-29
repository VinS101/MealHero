package dltone.com.mealhero;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ibm.mobile.services.data.IBMDataObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

/**
 * costin 11/6/2015
 */

public class VolunteerEditActivity extends Activity
{
    String CLASS_NAME = this.getClass().getName();

    //Volunteer reference
    Volunteer volunteer;
    //Original Volunteer
    Volunteer originalVolunteer;

    //UI Elements
    EditText nameTextBox;
    //EditText userNameTextBox;
    EditText passwordTextBox;
    EditText emailTextBox;
    EditText permissionTextBox;
    Button assignClientsButton;

    //App reference
    MealHeroApplication MHApp;

    //Action Mode
    ActionMode mActionMode;

    //Action Mode Callback
    ActionMode.Callback mActionModeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_volunteer);

        //Get references to UI elements
        nameTextBox = (EditText) findViewById(R.id.volunteer_edit_name_box);
        //userNameTextBox = (EditText) findViewById(R.id.volunteer_edit_username_box);
        emailTextBox = (EditText) findViewById(R.id.volunteer_edit_email_box);
        passwordTextBox = (EditText) findViewById(R.id.volunteer_edit_password_box);
        passwordTextBox.setEnabled(false);
        permissionTextBox = (EditText) findViewById(R.id.volunteer_edit_permission_box);
        assignClientsButton = (Button) findViewById(R.id.volunteer_edit_button);

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

        //Implement Contextual Action Bar
        mActionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle("Edit Volunteer");
                getMenuInflater().inflate(R.menu.menu_edit_volunteer, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.menu_delete_volunteer:
                    {

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
                            Toast.makeText(getApplicationContext(), "Volunteer delete successful!", Toast.LENGTH_SHORT).show();
                            MHApp.getVolunteerList().remove(volunteer);
                            MHApp.setVolunteerList(VolunteerProvider.GetVolunteers());
                            finish();
                            return true;
                        }
                    }
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
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
                mode.finish();
            }
        };

        //Get Action Mode and show Contextual Action Bar
        mActionMode = startActionMode(mActionModeCallback);
    }

    private void saveVolunteer(Volunteer v) {

        v.setName(nameTextBox.getText().toString());
        v.setEmail(emailTextBox.getText().toString());
        v.setPermission(permissionTextBox.getText().toString());
        //v.setClientList();
    }
}
