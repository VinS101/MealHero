package dltone.com.mealhero;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.Map;

/**
 * Created by costin on 10/22/2015.
 */
public class ClientEditActivity extends AppCompatActivity
{
    Map<String,String> clientInfo;

    //UI Elements
    ImageView userImage;
    EditText nameTextBox;
    EditText addressTextBox;
    EditText ageTextBox;
    EditText dietTextBox;

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

        //Get Selected Client
        clientInfo = (Map<String, String>) getIntent().getSerializableExtra("CLIENT");

        //Set UI values
        nameTextBox.setText(clientInfo.get("Name"));
        addressTextBox.setText(clientInfo.get("Address"));
        ageTextBox.setText(clientInfo.get("Age"));
        dietTextBox.setText(clientInfo.get("Diet"));

    }

}
