package dltone.com.mealhero;

import android.util.Log;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * w-garcia 10/1/2015.
 * S-Shahrdar 10/6/2015
 * costin 11/6/2015
 */
@IBMDataObjectSpecialization("Volunteer")
public class Volunteer  extends IBMDataObject implements Serializable//, Parcelable
{
    //region variables
    public static final String CLASS_NAME = "Volunteer";

    private static final String NAME = "Name";
    private static final String PASSWORD = "Password";
    private static final String EMAIL = "Email";
    private static final String PERMISSION = "Permission";
    private static final String LISTCLIENTS = "ListClients";

    //endregion

    public Volunteer() {}

    public Volunteer(String vName, String vPassword, String vEmail, String vPermission, ArrayList<String> vListClientIds)
    {
        setName(vName);
        setPassword(vPassword);
        setEmail(vEmail);
        setPermission(vPermission);
        setClientList(vListClientIds);
    }

    //region Accessors
    @Override
    public boolean equals(Object other) {
        if(this.getName().equals(((Volunteer)other).getName()) && this.getPassword().equals(((Volunteer) other).getPassword())
                && this.getEmail().equals(((Volunteer) other).getEmail()) && this.getPermission().equals(((Volunteer) other).getPermission())
                && this.getClientIdsList().equals(((Volunteer) other).getClientIdsList())) {
            return true;
        }

        return false;
    }
    @Override
    public String toString()
    {
        return (String) getObject(EMAIL);
    }
    /**
     * Gets volunteer's name
     * @return Name
     */
    public String getName()
    {
        return (String) getObject(NAME);
    }

    /**
     * Gets volunteer's password
     * @return Password
     */
    public String getPassword()
    {
        return (String) getObject(PASSWORD);
    }

    /**
     * Gets volunteer's email
     * @return Email
     */
    public String getEmail()
    {
        return (String) getObject(EMAIL);
    }

    /**
     * Gets volunteer's permission setting
     * @return Permission
     */
    public String getPermission()
    {
        return (String) getObject(PERMISSION);
    }

    /**
     * Gets volunteer's client ids list.
     * @return List of Client IDs
     */
    public ArrayList<String> getClientIdsList() {
        JSONArray json = (JSONArray) getObject(LISTCLIENTS);
        ArrayList<String> ids = new ArrayList<>();
        for(int i = 0; i < json.length(); i++) {
            try {
                ids.add(json.get(i).toString());
            } catch (Exception e) {
                Log.e(getClassName(), e.getMessage());
            }
        }
        return ids;
    }

    //endregion

    //region Mutators

    /**
     * Sets the volunteer's name
     * @param volunteerName
     */

    public void setName(String volunteerName)
    {
        setObject(NAME, (volunteerName != null) ? volunteerName : "" );
    }

    public void setEmail(String email) {
        setObject(EMAIL, (email != null) ? email : "");
    }

    private void setPassword(String password) { setObject(PASSWORD, (password != null) ? password : "");}

    public void setPermission(String permission) {
        setObject(PERMISSION, (permission != null) ? permission : "");
    }

    public void setClientList(ArrayList<String> list)
    {
        JSONArray array = new JSONArray(list);
        setObject(LISTCLIENTS, array);
    }


    //endregion

}
