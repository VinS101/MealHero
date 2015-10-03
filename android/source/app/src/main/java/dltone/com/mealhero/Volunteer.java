package dltone.com.mealhero;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

/**
 * w-garcia 10/1/2015.
 */
@IBMDataObjectSpecialization("Volunteer")
public class Volunteer  extends IBMDataObject
{
    public static final String CLASS_NAME = "Volunteer";
    String Name = "";
    String Username = "";
    String Password = "";
    Boolean IsOnline = false;
    String Email = "";
    public Volunteer() {}
    public Volunteer(String vName, String vUsername, String vPassword, String vEmail)
    {
        Name = vName;
        Username = vUsername;
        Password = vPassword;
        Email = vEmail;
    }
    public String getName()
    {
        return (String) getObject(Name);
    }
    public void setName(String volunteerName)
    {
        setObject(Name, (volunteerName != null) ? volunteerName : "" );
    }


}
