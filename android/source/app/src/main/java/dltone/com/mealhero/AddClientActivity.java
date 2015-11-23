package dltone.com.mealhero;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private ArrayList<Address> _addressResultList;

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
                _Address = _addressResultList.get(index);
                EditText cAddress = (EditText)findViewById(R.id._txtAddress);
                String fullAddress = String.format("%s, %s, %s", _Address.getAddressLine(0), _Address.getAddressLine(1), _Address.getAddressLine(2));
                cAddress.setText(String.format(fullAddress));
                break;
        }
    }

    public void OnSubmit(View view)
    {
        EditText cName = (EditText)findViewById(R.id._txtName);
        String Name = cName.getText().toString();

        EditText cDiet = (EditText)findViewById(R.id._txtDiet);
        String Diet = cDiet.getText().toString();

        EditText cAge = (EditText)findViewById(R.id._txtAge);
        String Age = cAge.getText().toString();

        EditText cAddress = (EditText)findViewById(R.id._txtAddress);

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
            String fullAddress = String.format("%s, %s, %s", _Address.getAddressLine(0), _Address.getAddressLine(1), _Address.getAddressLine(2));

            Client clientToSubmit = new Client(Name, fullAddress, Diet, Age, false, _Address.getLatitude(), _Address.getLongitude());
            ClientProvider.RegisterClient(clientToSubmit);
            Toast.makeText(this, "Client Sucessfully Added", Toast.LENGTH_LONG).show();
            _MHA = (MealHeroApplication) getApplication();
            _MHA.setClientList(ClientProvider.GetClients());
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_client, menu);
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

}
