package dltone.com.mealhero;

import android.app.Application;
import android.util.Log;

import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Owner on 10/7/2015.
 */
public class ClientProvider
{
    private static String CLASS_NAME = MealHeroApplication.class.getSimpleName();
    private static ClientProvider clientProvider = new ClientProvider();

    private ClientProvider(){ }

    public static ClientProvider getInstance()
    {
        return clientProvider;
    }

    public static List<Client> GetClients()
    {
        final List<Client> clientList = new ArrayList<>();
        try
        {
            IBMQuery<Client> query = IBMQuery.queryForClass(Client.class);

            // Query all the Volunteer objects from the server
            // create continuation to resume the process of querying server from where we left off previously
            query.find().continueWith(new Continuation<List<Client>, Void>()
            {
                @Override
                public Void then(Task<List<Client>> task) throws Exception
                {
                    final List<Client> objects = task.getResult();
                    // Log if the find was cancelled.
                    if (task.isCancelled())
                    {
                        Log.e(CLASS_NAME, "Exception: Task " + task.getError().getMessage());
                    }
                    else if (task.isFaulted())
                    {
                        Log.e(CLASS_NAME, "Exception: Task " + task.getError().getMessage());
                    }
                    else
                    {
                        // result success, load that list!
                        // Clear local itemList
                        clientList.clear();
                        for (IBMDataObject item : objects)
                        {
                            clientList.add((Client)item);
                        }

                        Log.e(CLASS_NAME, "Finished query for clients with " + clientList.size() + " items.");
                        // sortItems(volunteerList);
                        // List view thing -> lvArrayAdapter.notifyDataSetChanged();
                    }
                    return null;
                }

            }, Task.UI_THREAD_EXECUTOR); // end continutation definition

            query.find().waitForCompletion();
        }
        catch (IBMDataException e)
        {
            Log.e(CLASS_NAME, "Exception: " + e.getMessage());
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return clientList;
    }

    public static void RegisterClient(Client clientToBeRegistered) {
        if (clientToBeRegistered == null) return;

        SaveClient(clientToBeRegistered);
    }

    public static void SaveClient(Client clientToSave) {
        if(clientToSave == null) return;

        clientToSave.save().continueWith(new Continuation<IBMDataObject, Void>()
        {
            @Override
            public Void then(Task<IBMDataObject> task) throws Exception
            {
                if (task.isCancelled())
                {
                    Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                }
                else if (task.isFaulted())
                {
                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                }
                else
                {

                }
                return null;
            }
        });

        try
        {
            clientToSave.save().waitForCompletion();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void DeleteClient(Client clientToBeDeleted) {
        if (clientToBeDeleted == null) return;

        clientToBeDeleted.delete().continueWith(new Continuation<IBMDataObject, Void>()
        {
            @Override
            public Void then(Task<IBMDataObject> task) throws Exception
            {
                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                } else {

                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);

        try
        {
            clientToBeDeleted.delete().waitForCompletion();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static ArrayList<Client> GetAssignedClients(Volunteer v, MealHeroApplication app)
    {
        ArrayList<Client> assignedClients = new ArrayList<>();

        if (v == null)
        {
            return assignedClients;
        }

        ArrayList<String> ids = v.getClientIdsList();
        ArrayList<Client> clients = (ArrayList<Client>) app.getClientList();

        for(int i = 0; i < clients.size(); i++)
        {
            for(int j = 0; j < ids.size(); j++)
            {
                if(clients.get(i).getObjectId().equals(ids.get(j)))
                {
                    assignedClients.add(clients.get(i));
                }
            }
        }
        return assignedClients;
    }

    public static ArrayList<String> GetClientIds(ArrayList<Client> clients) {
        ArrayList<String> ids = new ArrayList<>();
        for(int i = 0; i < clients.size(); i++) {
            ids.add(clients.get(i).getObjectId());
        }

        return ids;
    }
}
