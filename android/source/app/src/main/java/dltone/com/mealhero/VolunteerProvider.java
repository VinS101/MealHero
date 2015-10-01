package dltone.com.mealhero;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

/**
 * w-garcia 10/1/2015.
 */
public class VolunteerProvider
{
    private String CLASS_NAME = MealHeroApplication.class.getSimpleName();

    public List<Volunteer> GetVolunteers()
    {
        final List<Volunteer> volunteerList = new ArrayList<Volunteer>();
        try
        {
            IBMQuery<Volunteer> query = IBMQuery.queryForClass(Volunteer.class);
            // Query all the Volunteer objects from the server
            // create continuation to resume the process of querying server from where we left off previously
            query.find().continueWith(new Continuation<List<Volunteer>, Void>()
            {
                @Override
                public Void then(Task<List<Volunteer>> task) throws Exception
                {
                    final List<Volunteer> objects = task.getResult();
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
                        volunteerList.clear();
                        for (IBMDataObject item : objects)
                        {
                            volunteerList.add((Volunteer)item);
                        }
                        // sortItems(volunteerList);
                        // List view thing -> lvArrayAdapter.notifyDataSetChanged();
                    }
                    return null;
                }

            }, Task.UI_THREAD_EXECUTOR); // end continutation definition
        }
        catch (IBMDataException e)
        {
            Log.e(CLASS_NAME, "Exception: " + e.getMessage());
        }

        return volunteerList;
    }
}
