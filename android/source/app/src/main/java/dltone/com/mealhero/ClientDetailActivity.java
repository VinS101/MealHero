package dltone.com.mealhero;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by costin on 12/2/2015.
 */
public class ClientDetailActivity extends AppCompatActivity {

    //Local copy of Client
    Client client;

    //App Reference
    MealHeroApplication MHApp;

    //UI Elements
    ImageView userImage;
    TextView nameTextView;
    TextView addressTextView;
    TextView ageTextView;
    TextView dietTextView;
    ListView logsListView;

    private ClientDetailLogsAdapter logsAdapter; //holds the list of notes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);

        //Get references to UI elements
        userImage = (ImageView) findViewById(R.id.client_detail_image);
        nameTextView = (TextView) findViewById(R.id.client_detail_name);
        addressTextView = (TextView) findViewById(R.id.client_detail_address_box);
        ageTextView = (TextView) findViewById(R.id.client_detail_age_box);
        dietTextView = (TextView) findViewById(R.id.client_detail_diet_box);
        logsListView =(ListView) findViewById(R.id.logView);
        //Get App Reference
        MHApp = (MealHeroApplication) getApplication();

        //Get Selected Client
        String clientId = getIntent().getStringExtra("ClientID");
        for(int i = 0; i < MHApp.getClientList().size(); i++) {
            if(MHApp.getClientList().get(i).getObjectId().equals(clientId)) {
                client = MHApp.getClientList().get(i);
                break;
            }
        }


        //Test
        client.appendLog("Test1");
        client.appendLog("Test2");
        client.appendLog("Test3");
        //Set up array adapter
        logsAdapter = new ClientDetailLogsAdapter(this, (client.getLogs()));
        logsListView.setAdapter(logsAdapter);

        //Set UI Values
        if(client != null) {
            nameTextView.setText(client.getName());
            addressTextView.setText(client.getAddress());
            ageTextView.setText(client.getAge());
            dietTextView.setText(client.getDiet());
        }
        else {
            Toast.makeText(this, "Client not found. Info unavailable.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_client_detail, menu);
        return true;
    }
}
