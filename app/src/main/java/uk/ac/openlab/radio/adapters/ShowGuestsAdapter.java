package uk.ac.openlab.radio.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

/**
 * Created by deepaksood619 on 28/6/16.
 */
public class ShowGuestsAdapter extends RecyclerView.Adapter<ShowGuestsAdapter.ViewHolder>{
    private ArrayList<String> guestsArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageButton imageButton;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.tv_show_listeners_phone_item);
            imageButton = (ImageButton) view.findViewById(R.id.ib_delete_listener);
        }
    }

    public ShowGuestsAdapter(ArrayList<String> guestsArrayList) {
        this.guestsArrayList = guestsArrayList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.show_listeners_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String guestNumber = guestsArrayList.get(position);
        holder.textView.setText(guestNumber);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FreeSwitchApi.shared().deleteListener(new IMessageListener() {
                    @Override
                    public void success() {
                        guestsArrayList.remove(position);
                        notifyItemRemoved(position);
                    }

                    @Override
                    public void fail() {

                    }

                    @Override
                    public void error() {

                    }

                    @Override
                    public void message(String message) {

                    }
                }, guestNumber);
            }
        });

    }

    @Override
    public int getItemCount() {
        return guestsArrayList.size();
    }
}
