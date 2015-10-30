package dltone.com.mealhero;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.ibm.mobile.services.data.IBMDataObject;

import java.util.ArrayList;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by costin on 10/22/2015.
 */
public class ClientEditActivity extends AppCompatActivity
{
    String CLASS_NAME = this.getClass().getName();

    Map<String,String> clientInfo;

    //UI Elements
    ImageView userImage;
    EditText nameTextBox;
    EditText addressTextBox;
    EditText ageTextBox;
    EditText dietTextBox;
    Button doneButton;

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
        doneButton = (Button) findViewById(R.id.client_edit_button);

        //Get Selected Client
        clientInfo = (Map<String, String>) getIntent().getSerializableExtra("CLIENT");

        //Set UI values
        nameTextBox.setText(clientInfo.get("Name"));
        addressTextBox.setText(clientInfo.get("Address"));
        ageTextBox.setText(clientInfo.get("Age"));
        dietTextBox.setText(clientInfo.get("Diet"));

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ////Get reference to Meal Hero App
                final MealHeroApplication MHApp = (MealHeroApplication) getApplication();
                //Get list of Clients
                ArrayList<Client> clients = (ArrayList) MHApp.getClientList();

                Client clientToFind = new Client(clientInfo.get("Name"), clientInfo.get("Address"), clientInfo.get("Diet"), clientInfo.get("Age"));
                Client clientToEdit = null;
                for(int i = 0; i < clients.size(); i++) {
                    if(clients.get(i).equals(clientToFind)) {
                        clientToEdit = clients.get(i);
                        break;
                    }
                }

                if(clientToEdit != null) {
                    clientToEdit.setName(nameTextBox.getText().toString());
                    clientToEdit.setAddress(addressTextBox.getText().toString());
                    clientToEdit.setDietPreference(dietTextBox.getText().toString());
                    clientToEdit.setAge(ageTextBox.getText().toString());
                    clientToEdit.save().continueWith(new Continuation<IBMDataObject, Void>() {
                        @Override
                        public Void then(Task<IBMDataObject> task) throws Exception {
                            if (task.isCancelled()) {
                                Log.e(CLASS_NAME, "Exception: " + task.toString() + " was cancelled.");
                            } else if (task.isFaulted()) {
                                Log.e(CLASS_NAME, "Exception: " + task.getError().getMessage());
                            } else {
                                Intent returnIntent = new Intent();
                                setResult(MHApp.EDIT_ACTIVITY_RC, returnIntent);
                                Toast.makeText(getApplicationContext(), "Client edit successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            return null;
                        }

                    }, Task.UI_THREAD_EXECUTOR);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error! Could not save changes!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
