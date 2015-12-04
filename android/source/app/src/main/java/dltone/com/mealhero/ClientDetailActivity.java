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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    TextView logsListView;


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
        logsListView =(TextView) findViewById(R.id.logView);
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


        //This is how logs are added for a client
        //client.appendLog("Test1");
        //client.appendLog("Test2");
        //client.appendLog("Test3");

        //For demonseration purposes. Will be opolete after logging in navigation
        displayLogPrototype();
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

    /**
     * For debugging purposes
     */
    private void displayLogPrototype()
    {
        SimpleDateFormat sm = new SimpleDateFormat("MM-dd-yyyy");
        Date d = new Date();
        String timestamp = "["+ sm.format(d) + "] ";

        StringBuilder s = new StringBuilder();
        s.append(timestamp + "log1");
        s.append("\n");
        s.append(timestamp +"log2");
        s.append("\n");
        s.append(timestamp +"log3");
        s.append("\n");
        s.append(timestamp +"log4");
        s.append("\n");
        s.append(timestamp +"log5");
        s.append("\n");
        s.append(timestamp +"log6");
        s.append("\n");
        s.append(timestamp +"log7");
        s.append("\n");
        s.append(timestamp +"log8");
        s.append("\n");
        s.append(timestamp +"log9");
        s.append("\n");
        s.append(timestamp +"log10");
        s.append("\n");
        s.append(timestamp +"log11");

        logsListView.setText(s.toString());
    }
}
