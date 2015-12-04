package dltone.com.mealhero;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.skobbler.ngx.navigation.SKNavigationSettings;

public class SettingsActivity extends AppCompatActivity
{

    MealHeroApplication MHA;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MHA = (MealHeroApplication) getApplication();

        Switch navSwitch = (Switch) findViewById(R.id._switchNavMode);
        navSwitch.setChecked(MHA.settings_isNavChecked);
        navSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    MHA.NavigationType = SKNavigationSettings.SKNavigationType.SIMULATION;
                    MHA.settings_isNavChecked = true;
                }
                else
                {
                    MHA.NavigationType = SKNavigationSettings.SKNavigationType.REAL;
                    MHA.settings_isNavChecked = false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
