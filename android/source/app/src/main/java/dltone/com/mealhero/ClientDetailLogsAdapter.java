package dltone.com.mealhero;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Shervin on 12/3/2015.
 */
public class ClientDetailLogsAdapter extends ArrayAdapter<String>
{
    private final Context context;
    private final ArrayList<String> logs;

    public ClientDetailLogsAdapter(Context context ,ArrayList<String> logs)
    {
        super(context, R.layout.log_list_item, logs);
        this.context = context;
        this.logs = logs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //2. Get clientRowView from inflater
        View RowView = inflater.inflate(R.layout.log_list_item, parent, false);

        TextView LogView = (TextView) RowView.findViewById(R.id.log_list_item_Log);
        //for(String s: logs)
        //{
           // LogView.setText(s.toString());
        //}
        if(logs.get(position) != null)
        {
            LogView.setText(logs.get(position).toString());
        }
        return RowView;
    }

    public ArrayList<String> getLogs()
    {
        return logs;
    }
}
