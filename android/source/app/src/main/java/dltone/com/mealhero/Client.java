package dltone.com.mealhero;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import java.io.Serializable;

/**
 * Created by costin on 10/6/2015.
 */
@IBMDataObjectSpecialization("Client")
public class Client extends IBMDataObject implements Serializable
{
    private static final String NAME = "Name";
    private static final String ADDRESS = "Address";
    private static final String DIET_PREFERENCE = "Diet Preference";
    private static final String AGE = "Age";

    public Client() { }

    public Client(String pName, String pAddress, String pDiet, String pAge)
    {
        setObject(NAME, (pName != null) ? pName : "");
        setObject(ADDRESS, (pAddress != null) ? pAddress : "");
        setObject(DIET_PREFERENCE, (pDiet != null) ? pDiet : "");
        setObject(AGE, (pAge != null) ? pAge : "");
    }

    @Override
    public String toString()
    {
        return (String) getObject(NAME);
    }

    public String getName()
    {
        return (String) getObject(NAME);
    }
    public String getAddress()
    {
        return (String) getObject(ADDRESS);
    }
    public String getDietPreference()
    {
        return (String) getObject(DIET_PREFERENCE);
    }
    public String getAge()
    {
        return (String) getObject(AGE);
    }
}
