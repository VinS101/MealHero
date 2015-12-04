package dltone.com.mealhero;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;
import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

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
    private static final String ASSIGNEDTO = "AssignedTo";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String LOGS = "logs";

    private ArrayList<String> logs = new ArrayList<>();

    public Client() { }

    public Client(String pName, String pAddress, String pDiet, String pAge, boolean pIsAssigned, String pAssignedTo, Double pLatitude, Double pLongitude) {
        setName(pName);
        setAddress(pAddress);
        setDietPreference(pDiet);
        setAge(pAge);
        setAssigned(pIsAssigned);
        setAssignedTo(pAssignedTo);
        setLatitude(pLatitude);
        setLongitude(pLongitude);
    }

    @Override
    public String toString()
    {
        return (String) getObject(NAME);
    }

    @Override
    public boolean equals(Object other) {
        if(this.getName().equals(((Client) other).getName()) && this.getAddress().equals(((Client) other).getAddress())
                && this.getDiet().equals(((Client) other).getDiet()) && this.getAge().equals(((Client) other).getAge())
                && this.getAssigned() == ((Client) other).getAssigned() && this.getAssignedTo().equals(((Client) other).getAssignedTo())
                && this.getLatitude().equals(((Client) other).getLatitude()) && this.getLongitude().equals(((Client) other).getLongitude())) {
            return true;
        }

        return false;
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
    public String getAssignedTo() { return (String) getObject(ASSIGNEDTO); }
    public Double getLatitude()
    {
        return (Double) getObject(LATITUDE);
    }
    public Double getLongitude()
    {
        return (Double) getObject(LONGITUDE);
    }
    public ArrayList<String> getLogs()
    {
        return logs;
    }

    public void setName(String name) { setObject(NAME, (name != null) ? name : ""); }
    public void setDietPreference(String diet) { setObject(DIET, (diet != null) ? diet : ""); }
    public void setAge(String age) { setObject(AGE, (age != null) ? age : ""); }
    public void setAssigned(boolean isAssigned) { setObject(ISASSIGNED, isAssigned); }
    public void setAddress(String address) { setObject(ADDRESS, (address != null) ? address : ""); }
    public void setAssignedTo(String volunteerName) { setObject(ASSIGNEDTO, (volunteerName != null) ? volunteerName : "Not Assigned"); }
    public void setLongitude(Double longitude) { setObject(LONGITUDE, (longitude != null) ? longitude : -1.00); }
    public void setLatitude(Double latitude) { setObject(LATITUDE, (latitude != null) ? latitude : -1.00); }
    public void appendLog(String log)
    {
        logs.add(log);
        JSONArray array = new JSONArray(logs);
        setObject(LOGS, array);
    }


}
