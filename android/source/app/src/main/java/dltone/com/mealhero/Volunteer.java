package dltone.com.mealhero;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * w-garcia 10/1/2015.
 * S-Shahrdar 10/6/2015
 */
@IBMDataObjectSpecialization("Volunteer")
public class Volunteer  extends IBMDataObject
{
    //region variables
    public static final String CLASS_NAME = "Volunteer";
    private String Name = "";
    private String Username = "";
    private String Password = "";
    private Boolean IsOnline = false;
    private String Email = "";
    //endregion

    public Volunteer() {}

    public Volunteer(String vName, String vUsername, String vPassword, String vEmail)
    {
        Name = vName;
        Username = vUsername;
        Password = vPassword;
        Email = vEmail;
    }

    //region Accessors

    /**
     * Gets volunteer's name
     * @return Name
     */
    public String getName()
    {
        return (String) getObject(Name);
    }

    /**
     * Gets volunteer's username
     * @return Username
     */
    public String getUserName()
    {
        return this.Name;
    }

    /**
     * Gets volunteer's password
     * @return Password
     */
    public String getPassword()
    {
        return this.Password;
    }

    /**
     * Gets volunteer's email
     * @return Email
     */
    public String getEmail()
    {
        return this.Email;
    }

    /**
     * returns if the volunteer is online or not
     * @return isOnline
     */
    public boolean isOnline()
    {
        return this.isOnline();
    }
    //endregion

    //region Mutators

    /**
     * Sets the volunteer's name
     * @param volunteerName
     */
    public void setName(String volunteerName)
    {
        setObject(Name, (volunteerName != null) ? volunteerName : "" );
    }
    //endregion



}
