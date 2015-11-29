package dltone.com.mealhero;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by costin on 11/15/2015.
 */
public class AssignClientsActivity extends Activity {

    //Selected Color
    private int selectedColor = Color.rgb(100, 181, 246);

    //UI References
    private ListView mClientListView;

    //List of Assigned Clients (Coming from Volunteer IDs list)
    private ArrayList<Client> assignedClients;

    //List of Unassigned Clients
    private ArrayList<Client> clients;

    //List of Selected Clients
    private ArrayList<Client> selectedClients;

    //Adapter to List of Unassigned Clients
    private AssignClientListAdapter clientAdapter;

    //Action Mode
    ActionMode mActionMode;

    //Action Mode Callback
    ActionMode.Callback mActionModeCallback;

    //App Reference
    MealHeroApplication MHApp;

    //Selected Volunteer
    Volunteer volunteer;

    //Original Volunteer
    Volunteer originalVolunteer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_clients);

        //Get App Reference
        MHApp = (MealHeroApplication) getApplication();

        //Make sure the Client List is populated
        if(MHApp.getClientList().size() < 1) {
            MHApp.setClientList(ClientProvider.GetClients());
        }
        //Get Volunteer
        int index = getIntent().getIntExtra("ItemLocation", -1);
        if(index >= 0) {
            volunteer = MHApp.getVolunteerList().get(index);
            originalVolunteer = new Volunteer(volunteer.getName(), volunteer.getPassword(), volunteer.getEmail(), volunteer.getPermission(), volunteer.getClientIdsList());
        } else {
            volunteer = null;
            originalVolunteer = null;
        }
        //Initialize unassigned clients list
        clients = new ArrayList<>();

        //Initialize selected clients
        selectedClients = new ArrayList<>();

        //Get assigned clients
        assignedClients = ClientProvider.GetAssignedClients(volunteer, MHApp);

        //Get ListView
        mClientListView = (ListView) findViewById(R.id.assignClientsListView);
        mClientListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //Add assigned clients to selected list.
        if(volunteer != null) {
            selectedClients.addAll(assignedClients);
            //Add selected to top of unassigned list
            for(Client c : selectedClients) {
                clients.add(c);
            }
        }

        //Get list of Unassigned Clients
        if(MHApp.getClientList().size() > 0) {
            for (Client c : MHApp.getClientList()) {
                if (c != null && c.getAssigned() == false && !clients.contains(c)) {
                    clients.add(c);
                }
            }
        }

        //Set up array adapter
        clientAdapter = new AssignClientListAdapter(getApplicationContext(), clients);
        mClientListView.setAdapter(clientAdapter);

        //Make my own "Multi-Select" feature.
        mClientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!selectedClients.contains(clients.get(position))) {
                    selectedClients.add(clients.get(position));
                    view.setBackgroundColor(selectedColor);
                    clients.get(position).setAssigned(true);
                } else {
                    selectedClients.remove(clients.get(position));
                    view.setBackgroundColor(Color.TRANSPARENT);
                    clients.get(position).setAssigned(false);
                }
            }
        });

        //Notify adapter of data change
        clientAdapter.notifyDataSetChanged();

        //Implement Contextual Action Bar
        mActionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle("Assign Clients");
                getMenuInflater().inflate(R.menu.menu_assign_clients, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if(volunteer != null) {
                    volunteer.setClientList(ClientProvider.GetClientIds(selectedClients));
                    if(!volunteer.equals(originalVolunteer)) {
                        //Un-assign old clients
                        for (Client c : assignedClients) {
                            if (!selectedClients.contains(c)) {
                                c.setAssigned(false);
                                c.setAssignedTo("Not Assigned");
                            }
                            c.save();
                        }
                        //Assign new clients
                        for (Client c : selectedClients) {
                            c.setAssigned(true);
                            if (!volunteer.getName().isEmpty()) {
                                c.setAssignedTo("Assignee: " + volunteer.getName());
                            } else {
                                c.setAssignedTo("Assignee: " + volunteer.getEmail());
                            }
                            c.save();
                        }
                    }
                }
                Intent returnIntent = new Intent();
                setResult(MealHeroApplication.EDIT_ACTIVITY_RC, returnIntent);
                finish();
                mode.finish();
            }
        };

        //Get Action Mode and show Contextual Action Bar
        mActionMode = startActionMode(mActionModeCallback);
    }


}
