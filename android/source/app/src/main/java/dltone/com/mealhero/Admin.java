package dltone.com.mealhero;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;
import java.lang.UnsupportedOperationException;
/**
 * Created by Shervin.S on 10/6/2015.
 */
public class Admin extends Volunteer
{
    private static final String NAME = "Name";
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";
    private static final String EMAIL = "Email";

    /**
     * Default constructor to construct the Admin object
     * @param vName  Admin name
     * @param vUsername Admin username
     * @param vPassword Admin Password
     * @param vEmail Admin Email
     */
    public Admin(String vName, String vUsername, String vPassword, String vEmail)
    {
        setObject(NAME, (vName != null) ? vName : "");
        setObject(USERNAME, (vUsername != null) ? vUsername : "");
        setObject(PASSWORD, (vPassword != null) ? vPassword : "");
        setObject(EMAIL, (vEmail != null) ? vEmail : "");
    }

    //region Accessors
    /**
     * Gets Admin's name
     * @return Name
    */
    public String getName()
    {
        return (String) getObject(NAME);
    }

    /**
     * Gets Admin's username
     * @return Username
     */
    public String getUserName()
    {
        return (String) getObject(USERNAME);
    }

    /**
     * Gets Admin's password
     * @return Password
     */
    public String getPassword()
    {
        return (String) getObject(PASSWORD);
    }

    /**
     * Gets Admin's email
     * @return Email
     */
    public String getEmail()
    {
        return (String) getObject(EMAIL);
    }

    //endregion

    //region Mutators

    /**
     * Reset password if needed
     * @param currentPassword current admin's password
     * @param newPassword Admin's new password
     */
    public void resetPassword(String currentPassword, String newPassword)
    {
        throw new java.lang.UnsupportedOperationException(); //not implemented yet
    }
    //endregion
}
