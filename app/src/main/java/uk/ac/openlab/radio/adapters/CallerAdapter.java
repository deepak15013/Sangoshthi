package uk.ac.openlab.radio.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.datatypes.Caller;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;
import uk.ac.openlab.radio.network.MessageHelper;

/**
 * Created by kylemontague on 21/03/16.
 */
public class CallerAdapter extends RecyclerView.Adapter<CallerAdapter.ViewHolder> {


    private ArrayList<Caller> mDataset;
    private int mTint;
    private IRecyclerViewItemClickedListener listener;

    public CallerAdapter(ArrayList<Caller> data, IRecyclerViewItemClickedListener listener, int tint) {
        this.mDataset = data;
        if(this.mDataset!=null && this.mDataset.size()>1)
            Collections.sort(this.mDataset, new CallerComparator());
        this.listener = listener;
        this.mTint = tint;
    }


    /**
     * Add a new caller to the queue grouped by the topics
     * @param caller
     */
    public void addCaller(Caller caller){
        //todo need to factor in the topic ordering!!
        //place caller in the list order (grouped by topic)
//        for (int x = this.mDataset.size()-1; x >= 0; x--){
//            if(this.mDataset.get(x).getTopic() == caller.getTopic()){
//                this.mDataset.add(x+1,caller);
//                notifyItemInserted(x+1);
//                return;
//            }
//        }
        //no one else talking about this. add to the end of the list.

        if(caller == null || caller.getType() == Caller.TYPE.GUEST || caller.getType() == Caller.TYPE.PRESENTER)
            return;

        this.mDataset.add(caller);
        Collections.sort(this.mDataset, new CallerComparator());
        notifyItemInserted(this.mDataset.size()-1);
    }


    public void setDataset(ArrayList<Caller> callers){
        this.mDataset = new ArrayList<>();
        boolean hasChanged = false;
        if(callers!=null) {
            for(Caller c : callers){
                if(c.getType() != Caller.TYPE.GUEST && c.getType() != Caller.TYPE.PRESENTER)
                    this.mDataset.add(c);
                    hasChanged = true;
            }
            if(hasChanged)
                notifyDataSetChanged();
        }
    }

    /**
     * Remove a specific caller from the queue.
     * @param caller
     */
    public void removeCaller(Caller caller){
        int index = this.mDataset.indexOf(caller);
        this.mDataset.remove(index);
        notifyItemRemoved(index);

    }

    /**
     * Remove all callers in the queue waiting to speak about a specific topic.
     * @param topicID
     */
    public void removeTopicCallers(String topicID){
        boolean changes = false;
        for(Caller object:this.mDataset){
           if(object.getTopic() == topicID) {
               changes = true;
               this.mDataset.remove(object);
           }
        }
        if(changes)
            notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ToggleButton v = (ToggleButton)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.caller_button, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //todo set the image based on the mute state of the caller
        //todo also need to switch out image based on user role??
        Caller c = this.mDataset.get(position);
        if(c.isMutestate()) {
            holder.view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_phone_muted, 0, 0);
        }
        else
            holder.view.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_phone_active,0,0);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.recyclerViewItemClicked(v,position);
                toggleMute(position);
            }
        });
        holder.view.setBackgroundResource(mTint);
    }

    private void toggleMute(final int position) {
        Caller c = this.mDataset.get(position);
        MessageHelper.MuteState state = c.isMutestate()? MessageHelper.MuteState.unmute: MessageHelper.MuteState.mute;
        //FreeSwitchApi.shared().setCallerMute(c.getID(), state, muteListener); //todo remove hardcoding
    }

    @Override
    public int getItemCount() {
        return this.mDataset.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ToggleButton view;
        public ViewHolder(ToggleButton view) {
            super(view);
            this.view = view;
        }
    }


    public class CallerComparator implements Comparator<Caller> {

        @Override
        public int compare(Caller lhs, Caller rhs) {
            return ((Long)lhs.getTimestamp()).compareTo(rhs.getTimestamp());
        }
    }


    IMessageListener muteListener = new IMessageListener() {
        @Override
        public void success() {
            Log.d("Mute","Success");
        }

        @Override
        public void fail() {
            Log.d("Mute","Fail");
        }

        @Override
        public void error() {
            Log.d("Mute","Error");
        }

        @Override
        public void message(String message) {

            try {
                Caller result = Caller.fromJson(message);

                for (int x = 0; x < mDataset.size(); x++) {
                    Caller c = mDataset.get(x);
                    if (c.getID().equals(result.getID())) {
                        c.setMutestate(!c.isMutestate());
                        notifyItemChanged(x);
                    }
                }
            }catch (Exception e){
                Log.e("Mute",e.getMessage());
            }
        }
    };
}
