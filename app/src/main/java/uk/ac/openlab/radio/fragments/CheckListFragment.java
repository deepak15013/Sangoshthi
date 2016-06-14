/*
 * Copyright (c) 2016. Kyle Montague
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.ac.openlab.radio.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.adapters.CheckListAdapter;
import uk.ac.openlab.radio.adapters.IRecyclerViewItemClickedListener;
import uk.ac.openlab.radio.adapters.RHDCheckLists;

/**
 * Created by Kyle Montague on 29/02/16.
 */
public class CheckListFragment extends ListFragment implements IRecyclerViewItemClickedListener {

    private Button button;
    private ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_checklist, container, false);
        list = (ListView)view.findViewById(android.R.id.list);
        button = (Button)view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo button clicked.
                Toast.makeText(getActivity().getApplicationContext(),"button clicked ",Toast.LENGTH_LONG).show();

            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(),"List CLicked",Toast.LENGTH_LONG).show();

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //todo make onitemclick listener
                Toast.makeText(getActivity().getApplicationContext(),"Item clicked: "+position,Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //todo check if the fragment is for the main menu, or a different menu.
        CheckListAdapter adapter = RHDCheckLists.mainMenu(getActivity(), this);
//        setListAdapter(adapter);
    }

    @Override
    public void recyclerViewItemClicked(View view, int position) {
        //todo handle onitemclick events
    }
}
