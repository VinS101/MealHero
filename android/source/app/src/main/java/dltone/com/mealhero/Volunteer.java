package dltone.com.mealhero;

import android.os.Parcel;
import android.os.Parcelable;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";
    private static final String EMAIL = "Email";
    private static final String PERMISSION = "Permission";

    private static final String LISTCLIENTS = "ListClients";

    //endregion

    public Volunteer() {}

    public Volunteer(String vName, String vUsername, String vPassword, String vEmail, String vPermission)
    {
        setObject(NAME, (vName != null) ? vName : "");
        setObject(USERNAME, (vUsername != null) ? vUsername : "");
        setObject(PASSWORD, (vPassword != null) ? vPassword : "");
        setObject(EMAIL, (vEmail != null) ? vEmail : "");
        setObject(PERMISSION, (vPermission != null) ? vPermission : "");
        setObject(LISTCLIENTS, new ArrayList<Client>());
    }

    //region Accessors
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
     * Gets volunteer's username
     * @return Username
     */
    public String getUserName()
    {
        return (String) getObject(USERNAME);
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
     * Gets volunteer's permission setting. Make sure to cast each iterated object.
     * @return List of Clients
     */
    public List<?> getClientList() { return (List<?>) getObject(LISTCLIENTS); }

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

    public void setUsername(String userName) {
        setObject(USERNAME, (userName != null) ? userName : "" );
    }

    public void setEmail(String email) {
        setObject(EMAIL, (email != null) ? email : "");
    }

    public void setPermission(String permission) {
        setObject(PERMISSION, (permission != null) ? permission : "");
    }

    public void setClientList(List<Client> list)
    {
        setObject(LISTCLIENTS, list);
    }


    //endregion

}
