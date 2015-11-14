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

public class EditVolunteerActivity extends Activity
{
    String CLASS_NAME = this.getClass().getName();

    //Volunteer reference
    Volunteer volunteer;

    //UI Elements
    EditText nameTextBox;
    EditText userNameTextBox;
    EditText passwordTextBox;
    EditText emailTextBox;
    EditText permissionTextBox;
    Button assignClients;

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
        userNameTextBox = (EditText) findViewById(R.id.volunteer_edit_username_box);
        emailTextBox = (EditText) findViewById(R.id.volunteer_edit_email_box);
        passwordTextBox = (EditText) findViewById(R.id.volunteer_edit_password_box);
        passwordTextBox.setEnabled(false);
        permissionTextBox = (EditText) findViewById(R.id.volunteer_edit_permission_box);
        assignClients = (Button) findViewById(R.id.volunteer_edit_button);

        //Get App Reference
        MHApp = (MealHeroApplication) getApplication();

        //Get Volunteer Reference
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
                        if(volunteer != null) {
                            deleteVolunteer(volunteer);
                            Toast.makeText(getApplicationContext(), "Volunteer delete successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MHApp.getApplicationContext(), "Error! Could not delete client!", Toast.LENGTH_LONG).show();
                        }
                        Intent returnIntent = new Intent();
                        setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);
                        finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if (volunteer != null) {
                    saveVolunteer(volunteer);
                    Toast.makeText(getApplicationContext(), "Volunteer edit successful!", Toast.LENGTH_SHORT).show();
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

                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }
}
