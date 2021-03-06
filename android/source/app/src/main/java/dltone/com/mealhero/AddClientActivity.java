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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class AddClientActivity extends AppCompatActivity
{
    MealHeroApplication _MHA;
    private Address _Address;
    private View progressView;
    private View addClientView;
    private AddClientTask mAuthTask = null;
    private String Name;
    private String Diet;
    private String Age;
    private ArrayList<Address> _addressResultList;
    private EditText cName;
    private EditText cDiet;
    private EditText cAge;
    private EditText cAddress;
    private static final int MENU_ADMIN = Menu.FIRST;
    private static final int MENU_LOGOUT = Menu.FIRST + 1;
    public void OnVerifyAddress(View view)
    {
        EditText cAddress = (EditText)findViewById(R.id._txtAddress);
        String address = cAddress.getText().toString();

        if(address.isEmpty())
        {
            cAddress.setError("Address cannot be blank!");
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
                if(index >= 0) {
                    _Address = _addressResultList.get(index);
                    String fullAddress = String.format("%s, %s, %s", _Address.getAddressLine(0), _Address.getAddressLine(1), _Address.getAddressLine(2));
                    cAddress.setText(String.format(fullAddress));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void OnSubmit(View view)
    {
        Name = cName.getText().toString();
        Diet = cDiet.getText().toString();
        Age = cAge.getText().toString();

        boolean addClientKey = true;
        if(Name.isEmpty())
        {
            cName.setError("Name cannot be blank!");
            addClientKey = false;
        }
        if(Diet.isEmpty())
        {
            cDiet.setError("Diet cannot be null");
            addClientKey = false;
        }
        if(Age.isEmpty())
        {
            cAge.setError("Age cannot be empty");
            addClientKey = false;
        }
        if (_Address == null)
        {
            cAddress.setError("Address must be verified");
            addClientKey = false;
        }

        //Ready to add
        if(addClientKey)
        {
            //Show Spinning bar, run in the thread
            showProgress(true);
            mAuthTask = new AddClientTask("Add Client");
            mAuthTask.execute((Void) null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        progressView = findViewById(R.id.addClient_progress);
        addClientView = findViewById(R.id.addClient);

        cName = (EditText)findViewById(R.id._txtName);
        cDiet = (EditText)findViewById(R.id._txtDiet);
        cAge = (EditText)findViewById(R.id._txtAge);
        cAddress = (EditText)findViewById(R.id._txtAddress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_client, menu);
        return true;
    }

    /**
     * Gets called every time the user presses the menu button.
     * Use if your menu is dynamic.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        //if (mVolunteerToDisplay.getPermission().equalsIgnoreCase("Admin"))
        menu.add(0, MENU_ADMIN, Menu.NONE, R.string.action_admin);
        menu.add(0, MENU_LOGOUT, Menu.NONE, R.string.action_logout);

        getMenuInflater().inflate(R.menu.menu_add_client, menu);

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id)
        {
            case MENU_LOGOUT:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void logout()
    {
        Intent intent = new Intent(AddClientActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = 600; //long

            addClientView.setVisibility(show ? View.GONE : View.VISIBLE);
            progressView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            addClientView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class AddClientTask extends AsyncTask<Void, Void, Boolean>
    {
        AddClientTask(String role)
        {
            this.role = role;
        }
        Intent intent;
        @Override
        protected Boolean doInBackground(Void... params)
        {
            String fullAddress = String.format("%s, %s, %s", _Address.getAddressLine(0), _Address.getAddressLine(1), _Address.getAddressLine(2));

            Client clientToSubmit = new Client(Name, fullAddress, Diet, Age, false, "Not Assigned", _Address.getLatitude(), _Address.getLongitude());
            ClientProvider.RegisterClient(clientToSubmit);

            _MHA = (MealHeroApplication) getApplication();
            _MHA.setClientList(ClientProvider.GetClients());
            finish();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            mAuthTask = null;
            showProgress(false);
            Toast.makeText(_MHA.getApplicationContext(), "Client Successfully Added!", Toast.LENGTH_LONG).show();

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


