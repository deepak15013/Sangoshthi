package uk.ac.openlab.radio.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.datatypes.Topic;

/**
 * Created by kylemontague on 21/03/16.
 */
public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {


    private ArrayList<Topic> dataset ;

    public TopicAdapter(ArrayList<Topic> data) {
        this.dataset = data;
    }


    /**
     * Add a new topic to the dataset.
     * @param object
     */
    public void add(Topic object){
        this.dataset.add(object);
        notifyItemInserted(this.dataset.size()-1);
    }

    /**
     * Remove a specific topic from the dataset.
     * @param object
     */
    public void remove(Topic object){
        int index = this.dataset.indexOf(object);
        this.dataset.remove(index);
        notifyItemRemoved(index);

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Topic object = this.dataset.get(position);
        holder.imageView.setImageURI(Uri.parse(object.getIcon().getUri()));
        holder.textView.setText(object.getName());
        holder.view.setBackgroundResource(object.getTint());

    }

    @Override
    public int getItemCount() {
        return this.dataset.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout view;
        ImageView imageView;
        TextView textView;
        public ViewHolder(RelativeLayout view) {
            super(view);
            this.view = view;
            this.imageView = (ImageView) view.findViewById(R.id.icon);
            this.textView = (TextView) view.findViewById(R.id.title);
        }
    }
}
