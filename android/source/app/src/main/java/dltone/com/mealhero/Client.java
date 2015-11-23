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
    private static final String DIET = "Diet";
    private static final String AGE = "Age";
    private static final String ISASSIGNED = "IsAssigned";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";

    public Client() { }

    public Client(String pName, String pAddress, String pDiet, String pAge, boolean pIsAssigned, Double pLatitude, Double pLongitude) {
        setName(pName);
        setAddress(pAddress);
        setDietPreference(pDiet);
        setAge(pAge);
        setAssigned(pIsAssigned);
        setLatitude(pLatitude);
        setLongitude(pLongitude);
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
    public String getDiet()
    {
        return (String) getObject(DIET);
    }
    public String getAge()
    {
        return (String) getObject(AGE);
    }
    public boolean getAssigned() { return (boolean) getObject(ISASSIGNED); }
    public String getAddress()
    {
        return (String) getObject(ADDRESS);
    }
    public Double getLatitude()
    {
        return (Double) getObject(LONGITUDE);
    }
    public Double getLongitude()
    {
        return (Double) getObject(LONGITUDE);
    }

    public void setName(String name) { setObject(NAME, (name != null) ? name : ""); }
    public void setDietPreference(String diet) { setObject(DIET, (diet != null) ? diet : ""); }
    public void setAge(String age) { setObject(AGE, (age != null) ? age : ""); }
    public void setAssigned(boolean isAssigned) { setObject(ISASSIGNED, isAssigned); }
    public void setAddress(String address) { setObject(ADDRESS, (address != null) ? address : ""); }
    public void setLongitude(Double longitude) { setObject(LONGITUDE, (longitude != null) ? longitude : -1.00); }
    public void setLatitude(Double latitude) { setObject(LATITUDE, (latitude != null) ? latitude : -1.00); }
}
