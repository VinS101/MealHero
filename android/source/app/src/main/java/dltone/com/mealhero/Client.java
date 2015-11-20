package dltone.com.mealhero;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by costin on 10/6/2015.
 */
@IBMDataObjectSpecialization("Client")
public class Client extends IBMDataObject implements Serializable
{
    private static final String NAME = "Name";
    private static final String ADDRESS = "Address";
    private static final String DIET = "Diet";
    private static final String AGE = "Age";
    private static final String ISASSIGNED = "IsAssigned";


    public Client() { }

    public Client(String pName, String pAddress, String pDiet, String pAge, boolean pIsAssigned) {
        setName(pName);
        setAddress(pAddress);
        setDietPreference(pDiet);
        setAge(pAge);
        setAssigned(pIsAssigned);
    }

    @Override
    public String toString() {
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
    public String getDiet()
    {
        return (String) getObject(DIET);
    }
    public String getAge()
    {
        return (String) getObject(AGE);
    }
    public boolean getAssigned() { return (boolean) getObject(ISASSIGNED); }

    public void setName(String name) { setObject(NAME, (name != null) ? name : ""); }
    public void setAddress(String address) { setObject(ADDRESS, (address != null) ? address : ""); }
    public void setDietPreference(String diet) { setObject(DIET, (diet != null) ? diet : ""); }
    public void setAge(String age) { setObject(AGE, (age != null) ? age : ""); }
    public void setAssigned(boolean isAssigned) { setObject(ISASSIGNED, isAssigned); }

}
