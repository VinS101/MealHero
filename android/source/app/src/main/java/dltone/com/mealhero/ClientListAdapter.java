package dltone.com.mealhero;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by costin on 10/18/2015.
 */
public class ClientListAdapter extends ArrayAdapter<Client>
{
    private final Context context;
    private final ArrayList<Client> clientsArrayList;

    public ClientListAdapter(Context context, ArrayList<Client> clientsArrayList)
    {
        super(context, R.layout.client_list_item, clientsArrayList);

        this.context = context;
        this.clientsArrayList = clientsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //2. Get clientRowView from inflater
        View clientRowView = inflater.inflate(R.layout.client_list_item, parent, false);

        //3. Get the image, and two text views from the clientRowView
        ImageView imageView = (ImageView) clientRowView.findViewById(R.id.client_list_item_image);
        TextView nameTextView = (TextView) clientRowView.findViewById(R.id.client_list_item_name);
        TextView addressTextView = (TextView) clientRowView.findViewById(R.id.client_list_item_address);

        //4. Set values for image and text views
        // TODO change default user to image of client
        if(clientsArrayList.get(position) != null) {
            imageView.setImageResource(R.mipmap.default_user);
            nameTextView.setText(clientsArrayList.get(position).getName());
            addressTextView.setText(clientsArrayList.get(position).getAddress());
        }

        return clientRowView;
    }

    public ArrayList<Client> getClients () {
        return clientsArrayList;
    }

}
