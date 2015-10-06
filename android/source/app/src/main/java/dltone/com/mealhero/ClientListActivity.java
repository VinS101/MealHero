package dltone.com.mealhero;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by costin on 10/6/2015.
 */
public class ClientListActivity extends Activity
{

    //UI References
    private Button mNavigationButton;
    private ListView mClientListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        //Get references to UI elements
        mNavigationButton = (Button) findViewById(R.id.startNavigationButton);
        mClientListView = (ListView) findViewById(R.id.clientListView);
    }
}
