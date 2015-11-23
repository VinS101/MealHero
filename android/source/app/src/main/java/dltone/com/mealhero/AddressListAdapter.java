package dltone.com.mealhero;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skobbler.ngx.search.SKSearchResult;

import java.util.ArrayList;

/**
 * Created by wgarcia on 11/17/2015.
 */
public class AddressListAdapter extends ArrayAdapter<Address>
{
    private final Context context;
    private final ArrayList<Address> addressArrayList;

    public AddressListAdapter(Context context, ArrayList<Address> AddressArrayList)
    {
        super(context, R.layout.address_list_item, AddressArrayList);

        this.context = context;
        this.addressArrayList = AddressArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //2. Get addressRowView from inflater
        View addressRowView = inflater.inflate(R.layout.address_list_item, parent, false);

        //3. Get the two text views from the addressRowView
        TextView houseNumStreetName = (TextView) addressRowView.findViewById(R.id.address_list_house_num_street);
        TextView cityName = (TextView) addressRowView.findViewById(R.id.address_list_city);

        //4. Set values for image and text views
        // TODO change default user to image of client
        houseNumStreetName.setText(addressArrayList.get(position).getAddressLine(0));
        cityName.setText(addressArrayList.get(position).getLocality());
        //TODO: addressTextView.setText(clientsArrayList.get(position).getAddress());

        return addressRowView;
    }
}
