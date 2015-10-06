package dltone.com.mealhero;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;
import java.lang.UnsupportedOperationException;
/**
 * Created by Shervin.S on 10/6/2015.
 */
public class Admin
{
    private String name; //Admin has a name?
    private String username;
    private String password;
    private Boolean IsOnline = false;
    private String email = "";
    private boolean isOnline = false;

    /**
     * Default constructor to construct the Admin object
     * @param Name  Admin name
     * @param Username Admin username
     * @param Password Admin Password
     * @param Email Admin Email
     */
    public Admin(String Name, String Username, String Password, String Email)
    {
        this.name = Name;
        this.username = Username;
        this.password = Password;
        this.email = Email;
    }

    //region Accessors
    /**
     * Gets Admin's name
     * @return Name
    */
    public String getName()
    {
        return this.name;
    }

    /**
     * Gets Admin's username
     * @return Username
     */
    public String getUserName()
    {
        return this.username;
    }

    /**
     * Gets Admin's password
     * @return Password
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * Gets Admin's email
     * @return Email
     */
    public String getEmail()
    {
        return this.email;
    }

    /**
     * returns if the Admin is online or not
     * @return isOnline
     */
    public boolean isOnline()
    {
        return this.isOnline;
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
