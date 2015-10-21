package dltone.com.mealhero;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddClientActivity extends AppCompatActivity
{
    MealHeroApplication _MHA;

    public void OnSubmit(View view)
    {
        EditText cName = (EditText)findViewById(R.id._txtName);
        String Name = cName.getText().toString();
        EditText cAddress = (EditText)findViewById(R.id._txtAddress);
        String Address = cAddress.getText().toString();
        EditText cDiet = (EditText)findViewById(R.id._txtDiet);
        String Diet = cDiet.getText().toString();
        EditText cAge = (EditText)findViewById(R.id._txtAge);
        String Age = cAge.getText().toString();

        boolean addClientKey = true;
        if(Name.isEmpty())
        {
            cName.setError("Name cannot be blank!");
            addClientKey = false;
        }
        if(Address.isEmpty())
        {
            cAddress.setError("Address cannot be blank!");
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

        //Ready to add
        if(addClientKey)
        {
            Client clientToSubmit = new Client(Name, Address, Diet, Age);
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
