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
    private static String NAME = "Name";
    private static String ADDRESS = "Address";
    private static String DIET = "Diet";
    private static String AGE = "Age";

    public Client() { }

    public Client(String pName, String pAddress, String pDiet, String pAge)
    {
        setName(pName);
        setAddress(pAddress);
        setDietPreference(pDiet);
        setAge(pAge);
    }

    @Override
    public String toString()
    {
        return (String) getObject(NAME);
    }
    public HashMap<String, String> toHashMap()
    {
        HashMap<String, String> clientMap = new HashMap<String, String>();
        clientMap.put("Name", getName());
        clientMap.put("Address", getAddress());
        clientMap.put("Age", getAge());
        clientMap.put("Diet", getDiet());

        return clientMap;
    }

    @Override
    public boolean equals(Object other)
    {
        if(this == other)
            return true;

        if(!(other.getClass() == this.getClass()))
            return false;

        Client c = (Client) other;

        if(this.getName().equals(c.getName()) && this.getAddress().equals(c.getAddress()) && this.getAge().equals(c.getAge())) {
            return true;
        }
        else return false;
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
    public void setName(String name) { setObject(NAME, (name != null) ? name : ""); }
    public void setAddress(String address) { setObject(ADDRESS, (address != null) ? address : ""); }
    public void setDietPreference(String diet) { setObject(DIET, (diet != null) ? diet : ""); }
    public void setAge(String age) { setObject(AGE, (age != null) ? age : ""); }

}
