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

import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

/**
 * costin 11/6/2015
 */

public class EditVolunteerActivity extends AppCompatActivity
{
    String CLASS_NAME = this.getClass().getName();

    Volunteer volunteer;

    //UI Elements
    EditText nameTextBox;
    EditText userNameTextBox;
    EditText passwordTextBox;
    EditText emailTextBox;
    EditText permissionTextBox;
    Button assignClients;

    //APP
    MealHeroApplication MHApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_volunteer);

        //Get references to UI elements
        nameTextBox = (EditText) findViewById(R.id.volunteer_edit_name_box);
        userNameTextBox = (EditText) findViewById(R.id.volunteer_edit_username_box);
        emailTextBox = (EditText) findViewById(R.id.volunteer_edit_email_box);
        passwordTextBox = (EditText) findViewById(R.id.volunteer_edit_password_box);
        passwordTextBox.setEnabled(false);
        permissionTextBox = (EditText) findViewById(R.id.volunteer_edit_permission_box);
        assignClients = (Button) findViewById(R.id.volunteer_edit_button);

        //App
        MHApp = (MealHeroApplication) getApplication();

        //Get Volunteer
        int index = getIntent().getIntExtra("ItemLocation", 0);
        volunteer = MHApp.getVolunteerList().get(index);

        //Set UI Values
        if (volunteer != null) {
            nameTextBox.setText(volunteer.getName());
            userNameTextBox.setText(volunteer.getUserName());
            passwordTextBox.setText(volunteer.getPassword());
            emailTextBox.setText(volunteer.getEmail());
            permissionTextBox.setText(volunteer.getPermission());
        }

        assignClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Not yet implemented!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_volunteer, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                //Done editing. Save Volunteer.
                saveVolunteer(volunteer);
                return true;
            case R.id.menu_delete_volunteer:
                //Delete volunteer
                deleteVolunteer(volunteer);
                //Return to parent Activity
                return true;
            default:
                return false;
        }
    }

    private void saveVolunteer(Volunteer v) {

        v.setName(nameTextBox.getText().toString());
        v.setUsername(userNameTextBox.getText().toString());
        v.setEmail(emailTextBox.getText().toString());
        v.setPermission(permissionTextBox.getText().toString());
        //v.setClientList();

        v.save().continueWith(new Continuation<IBMDataObject, Void>() {
            @Override
            public Void then(Task<IBMDataObject> task) throws Exception {
                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception: " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception: " + task.getError().getMessage());
                } else {
                    Intent returnIntent = new Intent();
                    setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);
                    Toast.makeText(getApplicationContext(), "Volunteer edit successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return null;
            }

        }, Task.UI_THREAD_EXECUTOR);
    }
    private void deleteVolunteer(Volunteer v) {

        MHApp.getVolunteerList().remove(v);

        v.delete().continueWith(new Continuation<IBMDataObject, Void>() {
            @Override
            public Void then(Task<IBMDataObject> task) throws Exception {
                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                } else {
                    // Produce some callback of delete success
                    Intent returnIntent = new Intent();
                    setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);
                    Toast.makeText(getApplicationContext(), "Volunteer delete successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }
}
