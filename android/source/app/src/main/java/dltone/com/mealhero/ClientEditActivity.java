package dltone.com.mealhero;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.io.IOException;
import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by costin on 10/22/2015.
 */
public class ClientEditActivity extends AppCompatActivity
{
    private ArrayList<Address> _addressResultList;
    private Address _Address;

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

    Boolean addressVerified = true;
    private Boolean addressEdited = false;

    //Background Thread
    private DeleteClient mAuthTask = null;

    //misc
    private boolean isDeleted = false;

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

        TextWatcher textWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                addressVerified = false;
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (addressTextBox.getText().toString().equalsIgnoreCase(originalClient.getAddress().toString()))
                {
                    addressVerified = true;
                    addressEdited = false;
                }
            }
        };
        addressTextBox.addTextChangedListener(textWatcher);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (client != null)
                {
                    if (nameTextBox.getText().toString().isEmpty())
                    {
                        nameTextBox.setError("Name cannot be empty.");
                        return true;
                    }
                    else if (addressTextBox.getText().toString().isEmpty())
                    {
                        addressTextBox.setError("Address cannot be empty.");
                        return true;
                    }
                    else if (!addressVerified)
                    {
                        addressTextBox.setError("Address must be verified.");
                        return true;
                    }
                    else if (dietTextBox.getText().toString().isEmpty())
                    {
                        dietTextBox.setError("Diet cannot be empty.");
                        return true;
                    }
                    else if (ageTextBox.getText().toString().isEmpty())
                    {
                        ageTextBox.setError("Age cannot be empty.");
                        return true;
                    }

                    client.setName(nameTextBox.getText().toString());
                    client.setAddress(addressTextBox.getText().toString());
                    client.setDietPreference(dietTextBox.getText().toString());
                    client.setAge(ageTextBox.getText().toString());
                    if (addressEdited)
                    {
                        client.setLatitude(_Address.getLatitude());
                        client.setLongitude(_Address.getLongitude());
                    }

                    if(!client.equals(originalClient))
                    {
                        ClientProvider.SaveClient(client);
                        Toast.makeText(MHApp.getApplicationContext(), "Client changes saved!", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(MHApp.getApplicationContext(), "Error! Could not save changes!", Toast.LENGTH_LONG).show();
                }
                Intent returnIntent = new Intent();
                setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);
                finish();
                break;
            case R.id.menu_delete_client:
                showProgress(true);
                mAuthTask = new DeleteClient();
                mAuthTask.execute((Void) null);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                ClientProvider.DeleteClient(client);
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


    public void OnVerifyAddress(View view)
    {
        String address = addressTextBox.getText().toString();

        if(address.isEmpty())
        {
            addressTextBox.setError("Address cannot be blank!");
            return;
        }

        Geocoder geocoder = new Geocoder(getApplicationContext());
        try
        {
            _addressResultList = (ArrayList<Address>) geocoder.getFromLocationName(address, 20);
            if (_addressResultList.size() > 0)
            {
                Intent intent = new Intent(getApplicationContext(), AddressListActivity.class);
                intent.putParcelableArrayListExtra("Results_List", _addressResultList);
                startActivityForResult(intent, MealHeroApplication.EDIT_ACTIVITY_RC);
            }
            else
            {
                Toast.makeText(this, "No addresses could be found,", Toast.LENGTH_LONG).show();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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
                int index = data.getIntExtra("ItemLocation", -1);
                _Address = _addressResultList.get(index);
                String fullAddress = String.format("%s, %s, %s", _Address.getAddressLine(0), _Address.getAddressLine(1), _Address.getAddressLine(2));
                addressTextBox.setText(fullAddress);
                addressTextBox.setError(null);
                addressVerified = true;
                addressEdited = true;
                break;
        }
    }

}
