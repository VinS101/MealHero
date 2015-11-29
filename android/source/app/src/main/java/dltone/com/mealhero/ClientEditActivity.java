package dltone.com.mealhero;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.mobile.services.data.IBMDataObject;

import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by costin on 10/22/2015.
 */
public class ClientEditActivity extends Activity
{
    String CLASS_NAME = this.getClass().getName();

    //Local copy of Client
    Client client;

    //App reference
    MealHeroApplication MHApp;

    //Action Mode
    ActionMode mActionMode;

    //Action Mode Callback
    ActionMode.Callback mActionModeCallback;

    //UI Elements
    ImageView userImage;
    EditText nameTextBox;
    EditText addressTextBox;
    EditText ageTextBox;
    EditText dietTextBox;
    TextView assignedToTextView;

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

        //Get Selected Client
        int index = getIntent().getIntExtra("ItemLocation", -1);
        if(index >= 0) {
            client = MHApp.getClientList().get(index);
        } else {
            client = null;
        }

        //Set UI values
        nameTextBox.setText(client.getName());
        addressTextBox.setText(client.getAddress());
        ageTextBox.setText(client.getAge());
        dietTextBox.setText(client.getDiet());
        assignedToTextView.setText(client.getAssignedTo());

        //Implement Contextual Action Bar
        mActionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle("Edit Client");
                getMenuInflater().inflate(R.menu.menu_edit_client, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.menu_delete_client:
                        if(client != null) {
                            MHApp.getClientList().remove(client);
                            client.delete().continueWith(new Continuation<IBMDataObject, Void>() {
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
                            Toast.makeText(MHApp.getApplicationContext(), "Client deleted!", Toast.LENGTH_LONG).show();
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
                if (client != null) {
                    client.setName(nameTextBox.getText().toString());
                    //TODO: client.setAddress(addressTextBox.getText().toString());
                    client.setDietPreference(dietTextBox.getText().toString());
                    client.setAge(ageTextBox.getText().toString());
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
}
