package dltone.com.mealhero;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import java.text.SimpleDateFormat;
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
    TextView noteView;
    Button logButton;
    final StringBuilder note = new StringBuilder();

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
        logButton = (Button) findViewById(R.id.add_log_button);
        noteView = (TextView) findViewById(R.id.note_box);
        //Get App Reference
        MHApp = (MealHeroApplication) getApplication();

        //Get Selected Client
        String clientId = getIntent().getStringExtra("ClientID");
        for(int i = 0; i < MHApp.getClientList().size(); i++)
        {
            if(MHApp.getClientList().get(i).getObjectId().equals(clientId))
            {
                client = MHApp.getClientList().get(i);
                break;
            }
        }

        //Populate NotesBox
        String notes = client.getNotes();
        if (notes == null) notes = "";
        noteView.setText(notes);

        logButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getUserNote();
            }
        });


        //This is how logs are added for a client
        //client.appendLog("Test1");
        //client.appendLog("Test2");
        //client.appendLog("Test3");

        //For demonseration purposes. Will be opolete after logging in navigation
        //displayLogPrototype();
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

    private void addUserNote()
    {
        SimpleDateFormat sm = new SimpleDateFormat("MM-dd-yyyy");
        Date d = new Date();
        String timestamp = "["+ sm.format(d) + "] ";
        String vName = MHApp.currentLoggedInVolunteer.getName();
        if (vName == null || vName.isEmpty()) vName = MHApp.currentLoggedInVolunteer.getEmail();

        noteView.append(timestamp + vName + ": " + note.toString() + "\n");
        client.appendNote(noteView.getText().toString());
    }

    private void getUserNote()
    {
        note.setLength(0); //reset the buffer
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add note");
        alert.setMessage("Message");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                note.append(input.getText());
                addUserNote();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                // Canceled.
            }
        });

        alert.show();
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
