/*
 * Copyright (c) 2016. Kyle Montague
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.ac.openlab.radio.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.datatypes.CheckListItem;

/**
 * Created by Kyle Montague on 25/02/16.
 */
public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder>{

    ArrayList<CheckListItem> items;
    IRecyclerViewItemClickedListener listener;

    public CheckListAdapter(ArrayList<CheckListItem> items, IRecyclerViewItemClickedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public CheckListItem getItem(int position){
        if(position >=0 && position < items.size())
            return items.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public CheckListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout)LayoutInflater.from(parent.getContext()).inflate(R.layout.checklist_item_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        TextView title = (TextView) v.findViewById(R.id.title);
        ImageView icon = (ImageView) v.findViewById(R.id.icon);
        ViewHolder vh = new ViewHolder(v,title,icon);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CheckListAdapter.ViewHolder holder, final int position) {

        holder.position = position;
        holder.textView.setText(GlobalUtils.capitalizeWords(items.get(position).getTitle()));
        holder.icon.setImageResource(items.get(position).getIcon());

        holder.view.setEnabled(true);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.recyclerViewItemClicked(v,position);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView textView;
        ImageView icon;
        int position;

        public ViewHolder(View view,TextView textView, ImageView icon) {
            super(view);
            this.view = view;
            this.textView = textView;
            this.icon = icon;
        }

    }








}
