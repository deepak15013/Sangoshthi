package uk.ac.openlab.radio.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import uk.ac.openlab.radio.R;

/**
 * Created by deepaksood619 on 16/6/16.
 */
public class ShowListenersAdapter extends ArrayAdapter<String> {

    private int layoutResourceId;
    private Context context;
    private ArrayList<String> items;

    public ShowListenersAdapter(Context context, int layoutResourceId, ArrayList<String> items) {
        super(context, layoutResourceId, items);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        AtomPaymentHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        rowView = inflater.inflate(layoutResourceId, parent, false);

        holder = new AtomPaymentHolder();
        holder.phoneNum = items.get(position);
        holder.deleteListener = (ImageButton) rowView.findViewById(R.id.ib_delete_listener);
        holder.deleteListener.setTag(holder.phoneNum);

        holder.tvShowListenersPhoneItem = (TextView) rowView.findViewById(R.id.tv_show_listeners_phone_item);

        rowView.setTag(holder);

        setupItem(holder);
        return rowView;
    }

    private void setupItem(AtomPaymentHolder holder) {
        holder.tvShowListenersPhoneItem.setText(holder.phoneNum);
    }

    public static class AtomPaymentHolder {
        String phoneNum;
        TextView tvShowListenersPhoneItem;
        ImageButton deleteListener;
    }
}
