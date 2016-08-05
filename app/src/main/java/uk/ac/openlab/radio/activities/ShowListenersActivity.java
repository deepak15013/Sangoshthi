package uk.ac.openlab.radio.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.adapters.ShowListenersAdapter;
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

public class ShowListenersActivity extends AppCompatActivity {

    private static ArrayList<String> ashaArrayList, othersArrayList;

    private ChecklistItemView toolbarItemView;

    private ListView lvAshaListeners, lvOtherListeners;

    private ShowListenersAdapter ashaAdapter, othersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_listeners);

        toolbarItemView = (ChecklistItemView) findViewById(R.id.toolbar_item);
        assert toolbarItemView != null;
        toolbarItemView.setTitle(getResources().getString(R.string.show_listeners_title));
        toolbarItemView.setIcon(R.drawable.ic_person);

        ashaArrayList = new ArrayList<>();
        othersArrayList = new ArrayList<>();

        lvAshaListeners = (ListView) findViewById(R.id.lv_asha_listeners);
        lvOtherListeners = (ListView) findViewById(R.id.lv_other_listeners);

        getListeners();
    }

    private void getListeners() {
        FreeSwitchApi.shared().showListeners(new IMessageListener() {
            @Override
            public void success() {
                Log.v("tag", "listener list received");
            }

            @Override
            public void fail() {
                Log.v("tag", "failed");
            }

            @Override
            public void error() {
                Log.v("tag", "error");
            }

            @Override
            public void message(String message) {
                Log.v("tag", "message: "+message);
                parse(message);
                showListView();

            }
        });
    }

    public void removeListener (View v) {
        final String itemToRemove = (String)v.getTag();

        if(ashaArrayList.contains(itemToRemove)) {

            FreeSwitchApi.shared().deleteListener(new IMessageListener() {
                @Override
                public void success() {
                    ashaAdapter.remove(itemToRemove);
                    ashaArrayList.remove(itemToRemove);
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
            }, itemToRemove);
        }
        else if(othersArrayList.contains(itemToRemove)) {
            FreeSwitchApi.shared().deleteListener(new IMessageListener() {
                @Override
                public void success() {
                    othersAdapter.remove(itemToRemove);
                    othersArrayList.remove(itemToRemove);
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
            }, itemToRemove);
        }
        else
            Log.v("dks","item not found");

    }

    private void showListView() {
        ashaAdapter = new ShowListenersAdapter(ShowListenersActivity.this, R.layout.show_listeners_item, ashaArrayList);
        lvAshaListeners.setAdapter(ashaAdapter);
        othersAdapter = new ShowListenersAdapter(ShowListenersActivity.this, R.layout.show_listeners_item, othersArrayList);
        lvOtherListeners.setAdapter(othersAdapter);

        ListUtils.setDynamicHeight(lvAshaListeners);
        ListUtils.setDynamicHeight(lvOtherListeners);

    }

    public static void parse(String message) {
        if(ashaArrayList == null) {
            ashaArrayList = new ArrayList<>();
        }
        else {
            ashaArrayList.clear();
        }

        if(othersArrayList == null) {
            othersArrayList = new ArrayList<>();
        }
        else {
            othersArrayList.clear();
        }

        try {
            JSONObject rootObj = new JSONObject(message);

            JSONArray rootArr = rootObj.optJSONArray("results");

            for(int i=0; i<rootArr.length(); i++) {
                JSONObject categoryObject = rootArr.getJSONObject(i);
                JSONArray ashaListenersArr = categoryObject.optJSONArray("ASHA");
                JSONArray otherListenersArr = categoryObject.optJSONArray("OTHERS");

                if(ashaListenersArr != null) {
                    for(int j=0;j<ashaListenersArr.length(); j++) {
                        JSONObject listenerObject = ashaListenersArr.getJSONObject(j);
                        String phoneNum = listenerObject.optString("phone");
                        Log.v("dks", phoneNum);
                        ashaArrayList.add(phoneNum);
                    }
                }

                if(otherListenersArr != null) {
                    for(int j=0;j<otherListenersArr.length(); j++) {
                        JSONObject listenerObject = otherListenersArr.getJSONObject(j);
                        String phoneNum = listenerObject.optString("phone");
                        Log.v("dks", phoneNum);
                        othersArrayList.add(phoneNum);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void deleteAshaListeners(View view) {

        FreeSwitchApi.shared().deleteCategoryListener(new IMessageListener() {
            @Override
            public void success() {
                ashaArrayList.clear();
                ashaAdapter.clear();
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
        }, "ASHA");
    }

    public void deleteOtherListeners(View view) {

        FreeSwitchApi.shared().deleteCategoryListener(new IMessageListener() {
            @Override
            public void success() {
                othersArrayList.clear();
                othersAdapter.clear();
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
        }, "OTHERS");
    }

    public static int getAshaListeners() {
        if(ashaArrayList == null) {
            return 0;
        }
        return ashaArrayList.size();
    }

    public static int getOtherListeners() {
        if(othersArrayList == null) {
            return 0;
        }
        return othersArrayList.size();
    }

}

class ListUtils {
    public static void setDynamicHeight(ListView mListView) {
        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) {
            // when ashaAdapter is null
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }
}
