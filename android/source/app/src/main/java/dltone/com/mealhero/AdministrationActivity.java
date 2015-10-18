package dltone.com.mealhero;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class AdministrationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administration);
        ListView optionsView = (ListView) findViewById(R.id._lvwAdminOptions);
        optionsView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_administration, menu);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent = new Intent();
        switch(position)
        {
            case 0:
                break;
            case 1:
                intent = new Intent(AdministrationActivity.this, DisplayVolunteerViewActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(AdministrationActivity.this, AddClientActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(AdministrationActivity.this, ClientListActivity.class);
                startActivity(intent);
                break;
            case 4:
                break;
        }

    }
}
