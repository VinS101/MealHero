package dltone.com.mealhero;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import java.io.Serializable;

/**
 * w-garcia 10/1/2015.
 * S-Shahrdar 10/6/2015
 */
@IBMDataObjectSpecialization("Volunteer")
public class Volunteer  extends IBMDataObject implements Serializable
{
    //region variables
    public static final String CLASS_NAME = "Volunteer";

    private static final String NAME = "Name";
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";
    private static final String EMAIL = "Email";
    private static final String PERMISSION = "Permission";
    //endregion

    public Volunteer() {}

    public Volunteer(String vName, String vUsername, String vPassword, String vEmail, String vPermission)
    {
        setObject(NAME, (vName != null) ? vName : "");
        setObject(USERNAME, (vUsername != null) ? vUsername : "");
        setObject(PASSWORD, (vPassword != null) ? vPassword : "");
        setObject(EMAIL, (vEmail != null) ? vEmail : "");
        setObject(PERMISSION, (vPermission != null) ? vPermission : "");
    }

    //region Accessors

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


    //endregion



}
