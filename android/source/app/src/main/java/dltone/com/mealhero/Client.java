package dltone.com.mealhero;

/**
 * Created by costin on 10/6/2015.
 */
public class Client
{
    private String FirstName;
    private String LastName;
    private String Address;
    private String BirthDate;

    public Client(String pFirstName, String pLastName, String pBirthDate, String pAddress)
    {
        FirstName = (pFirstName != null) ? pFirstName : "";
        LastName = (pLastName != null) ? pLastName : "";
        BirthDate = (pBirthDate != null) ? pBirthDate : "";
        Address = (pAddress != null) ? pAddress : "";
    }

    public String getFirstName() {
        return FirstName;
    }
    public String getLastName() {
        return LastName;
    }
    public String getAddress() {
        return Address;
    }
    public String getBirthDate() {
        return BirthDate;
    }
}
