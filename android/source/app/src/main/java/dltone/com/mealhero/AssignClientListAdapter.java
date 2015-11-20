package dltone.com.mealhero;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by costin on 11/19/2015.
 */
public class AssignClientListAdapter extends ClientListAdapter {

    public AssignClientListAdapter(Context context, ArrayList<Client> clientsArrayList) {
        super(context, clientsArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        if(getClients().get(position).getAssigned()) {
            v.setBackgroundColor(Color.rgb(100, 181, 246));
        }

        return v;
    }
}
