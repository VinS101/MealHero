package dltone.com.mealhero;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

        Client clientToSubmit = new Client(Name, Address, Diet, Age);
        ClientProvider.RegisterClient(clientToSubmit);

        _MHA = (MealHeroApplication) getApplication();
        _MHA.setClientList(ClientProvider.GetClients());

        finish();
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
