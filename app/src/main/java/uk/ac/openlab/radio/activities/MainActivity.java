
/*
 * Copyright (c) 2016. Kyle Montague
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.ac.openlab.radio.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.adapters.CheckListAdapter;
import uk.ac.openlab.radio.adapters.IRecyclerViewItemClickedListener;
import uk.ac.openlab.radio.adapters.RHDCheckLists;
import uk.ac.openlab.radio.datatypes.CheckListItem;
import uk.ac.openlab.radio.drawables.ChecklistItemView;
import uk.ac.openlab.radio.network.AWSHandler;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;
import uk.ac.openlab.radio.network.MessageHelper;
import uk.ac.openlab.radio.services.ZMQSubscriber;
import uk.ac.openlab.radio.utilities.FileBrowserActivity;

public class MainActivity extends AppCompatActivity implements IRecyclerViewItemClickedListener {

    public static final String EXTRA_TITLES_ID = "EXTRA_TITLES_ID";
    public static final String EXTRA_ICONS_ID = "EXTRA_ICONS_ID";
    public static final String EXTRA_PAGE_ID = "EXTRA_PAGE_ID";
    public static final String EXTRA_TITLE_ITEM_TEXT = "EXTRA_TITLE_ITEM_TEXT";
    public static final String EXTRA_TITLE_ITEM_ICON = "EXTRA_TITLE_ITEM_ICON";
    public static final String EXTRA_TITLE_ITEM_STATE = "EXTRA_TITLE_ITEM_STATE";
    private static final String EXTRA_TITLE_ITEM_ID = "EXTRA_TITLE_ITEM_ID";

    private static final long TWENTY_SECOND_CLOCK = 20 *1000;

    private final int REQUEST_CODE_PICK_FILE = 2;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CheckListAdapter adapter;

    private Button button;
    private ChecklistItemView toolbarItemView;

    private int pageID = R.string.main_menu_title;

    public static AlertDialog alertDialogPlayTrailer;
    public static AlertDialog alertDialogCallCut;

    public static boolean uploadTrailer = false;

    // this thread is for getting the correct count of listeners and guests before starting the show.
    private Thread sleepThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GlobalUtils.appTheme());
        setContentView(R.layout.activity_checklist);

        if(getIntent().getBooleanExtra("EXIT",false)) {
            GlobalUtils.shared().setCallDisconnected(false);

            // This alertDialog is used to add the show to ivr library for later hearing.
            // Yes will take control to new activity where you can play the previous ivr or add new show to the ivr
            // No will exit the app

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.dialog_add_ivr_library);
            builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    FreeSwitchApi.shared().addIvrLibrary(new IMessageListener() {
                        @Override
                        public void success() {
                            Intent intent = new Intent(MainActivity.this, IvrLibraryActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void fail() {
                            Toast.makeText(MainActivity.this, R.string.toast_fail, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void error() {

                        }

                        @Override
                        public void message(String message) {

                        }
                    });

                }
            });

            builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog ivrDialog = builder.create();
            ivrDialog.show();

        }

        MessageHelper.shared().init(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarItemView = (ChecklistItemView)toolbar.findViewById(R.id.toolbar_item);
        recyclerView = (RecyclerView)findViewById(R.id.checkListView);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            int titles = extras.getInt(EXTRA_TITLES_ID,R.array.main_menu_titles);
            int icons = extras.getInt(EXTRA_ICONS_ID,R.array.main_menu_icons);
            pageID = extras.getInt(EXTRA_PAGE_ID,R.string.main_menu_title);
            String title = extras.getString(EXTRA_TITLE_ITEM_TEXT);
            boolean state = extras.getBoolean(EXTRA_TITLE_ITEM_STATE);
            int iconRes = extras.getInt(EXTRA_TITLE_ITEM_ICON);
            int id = extras.getInt(EXTRA_TITLE_ITEM_ID);
            if(title!=null){
                toolbarItemView.setTitle(title);
                toolbarItemView.setIcon(iconRes);
                toolbarItemView.setEnabled(false);
            }

            adapter = RHDCheckLists.listFromXML(this,getListener(pageID),titles,icons);

        }else{
            //default to using the main menu
            adapter = RHDCheckLists.mainMenu(getApplicationContext(), getListener(pageID));
            toolbarItemView.setTitle(pageID);
            toolbarItemView.setIcon(GlobalUtils.iconWithTint(getApplicationContext(),R.drawable.ic_radio,R.color.white));//todo swap for rhd logo
        }

        // Set a toolbar to replace the action bar.

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        button=  (Button)findViewById(R.id.bottomButton);
        setupUI(pageID);

        FreeSwitchApi.shared().init(getApplicationContext());
        AWSHandler.shared().init(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(GlobalUtils.shared().getCallDisconnected()) {

            FreeSwitchApi.shared().callRejected(new IMessageListener() {
                @Override
                public void success() {

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
            });

            GlobalUtils.shared().setCallDisconnected(false);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.dialog_call_disconnected);
            builder.setNegativeButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialogCallCut = builder.create();
            alertDialogCallCut.show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_stop:
                return true;
        }

        return super.onOptionsItemSelected(item); // important line
    }

    @Override
    public void recyclerViewItemClicked(View view, int position) {

    }

    protected void setupUI(int id){

        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(button.getText().equals(getString(R.string.action_quit))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.dialog_end_show_title);
                    builder.setMessage(R.string.dialog_end_show_message);
                    builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FreeSwitchApi.shared().cancelShow(new IMessageListener() {
                                @Override
                                public void success() {
                                    finish();
                                }

                                @Override
                                public void fail() {
                                    Toast.makeText(MainActivity.this, R.string.toast_fail, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void error() {

                                }

                                @Override
                                public void message(String message) {

                                }
                            });
                        }
                    });
                    builder.create().show();
                } else {
                    finish();
                }
            }
        };
        switch (id){
            case R.string.main_menu_title:
                button.setText(R.string.action_quit);
                break;
            case R.string.prepare_show_title:
                button.setText(R.string.action_finished);
                break;
            case R.string.edit_listeners_title:
                button.setText(R.string.action_finished);
                break;
            case R.string.edit_recording_title:
                button.setText(R.string.action_finished);
                break;
            case R.string.edit_content_title:
                button.setText(R.string.action_finished);
                break;
            case R.string.create_trailer_title:
                button.setText(R.string.action_finished);
                break;
            default:
                button.setVisibility(View.GONE);
                return;
        }
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(buttonListener);
    }


    protected IRecyclerViewItemClickedListener getListener(int id){
        switch (id){
            case R.string.main_menu_title:
                return mainMenuListener;
            case R.string.prepare_show_title:
                return prepareShowListener;
            case R.string.edit_listeners_title:
                return editListenersTitle;
            case R.string.edit_recording_title:
                return editRecordingTitle;
            case R.string.create_trailer_title:
                return createTrailerTitle;
            case R.string.edit_content_title:
                return editContentsTitle;
            case R.string.edit_guest_title:
                return editGuestTitle;
            default:
                return this;
        }
    }


    IRecyclerViewItemClickedListener mainMenuListener = new IRecyclerViewItemClickedListener() {
        @Override
        public void recyclerViewItemClicked(View view, int position) {
            Intent i =  null;
            ActivityOptionsCompat options;
            CheckListItem item = adapter.getItem(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                view.setTransitionName(getString(R.string.transition_name_listitem));

            switch(position) {
                case 0:
                    //prepare show
                    i = new Intent(MainActivity.this, MainActivity.class);
                    i.putExtra(EXTRA_TITLES_ID, R.array.prepare_show_titles);
                    i.putExtra(EXTRA_ICONS_ID, R.array.prepare_show_icons);
                    i.putExtra(EXTRA_PAGE_ID, R.string.prepare_show_title);
                    i.putExtra(EXTRA_TITLE_ITEM_TEXT, item.getTitle());
                    i.putExtra(EXTRA_TITLE_ITEM_ICON, item.getIcon());
                    i.putExtra(EXTRA_TITLE_ITEM_STATE, item.isComplete());
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, new Pair<>(view, getString(R.string.transition_name_listitem)));
                    ActivityCompat.startActivity(MainActivity.this, i, options.toBundle());

                    break;
                case 1:
                    //spread the word

                    Log.d("dks","adding progress bar");
                    progressBar.setVisibility(View.VISIBLE);

                    FreeSwitchApi.shared().checkTrailerStatus(new IMessageListener() {
                        @Override
                        public void success() {
                            Intent i;

                            // remove progress bar when new activity is being loaded.
                            progressBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("dks","removing progress bar");
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                            i = new Intent(MainActivity.this, SpreadTheWordActivity.class);
                            startActivity(i);
                        }

                        @Override
                        public void fail() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(R.string.dialog_create_trailer_first);
                            builder.setNegativeButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.create().show();
                        }

                        @Override
                        public void error() {

                        }

                        @Override
                        public void message(String message) {

                        }
                    });
                    break;

                case 2:
                    //run show

                    Log.d("dks","adding progress bar");
                    progressBar.setVisibility(View.VISIBLE);

                    getListeners();
                    getGuests();

                    ZMQSubscriber zmqSubscriber = new ZMQSubscriber();
                    zmqSubscriber.startSubscriber();

                    sleepThread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            finally {
                                Log.d("dks","Start Show Activity started");

                                // remove progress bar when new activity is being loaded.
                                progressBar.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("dks","removing progress bar");
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });

                                Intent intent = new Intent(MainActivity.this, ShowOverviewActivity.class);
                                startActivity(intent);
                            }

                        }
                    });
                    sleepThread.start();

                    break;
            }
        }
    };

    ActivityOptionsCompat options;
    IRecyclerViewItemClickedListener prepareShowListener = new IRecyclerViewItemClickedListener() {
        @Override
        public void recyclerViewItemClicked(View view, int position) {
            Intent i =  null;

            CheckListItem item = adapter.getItem(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                view.setTransitionName(getString(R.string.transition_name_listitem));

            switch(position){

                // Add Guest
                case 0:
                    i = new Intent(MainActivity.this,MainActivity.class);
                    i.putExtra(EXTRA_TITLES_ID,R.array.edit_guests_titles);
                    i.putExtra(EXTRA_ICONS_ID, R.array.record_topics_icons);
                    i.putExtra(EXTRA_PAGE_ID,R.string.edit_guest_title);
                    i.putExtra(EXTRA_TITLE_ITEM_TEXT,item.getTitle());
                    i.putExtra(EXTRA_TITLE_ITEM_ICON,item.getIcon());
                    i.putExtra(EXTRA_TITLE_ITEM_STATE,item.isComplete());
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, new Pair<>(view, getString(R.string.transition_name_listitem)));
                    ActivityCompat.startActivity(MainActivity.this, i, options.toBundle());
                    break;

                //Edit Listeners
                case 1:
                    i = new Intent(MainActivity.this,MainActivity.class);
                    i.putExtra(EXTRA_TITLES_ID,R.array.edit_listeners_titles);
                    i.putExtra(EXTRA_ICONS_ID, R.array.record_topics_icons);
                    i.putExtra(EXTRA_PAGE_ID,R.string.edit_listeners_title);
                    i.putExtra(EXTRA_TITLE_ITEM_TEXT,item.getTitle());
                    i.putExtra(EXTRA_TITLE_ITEM_ICON,item.getIcon());
                    i.putExtra(EXTRA_TITLE_ITEM_STATE,item.isComplete());
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, new Pair<>(view, getString(R.string.transition_name_listitem)));
                    ActivityCompat.startActivity(MainActivity.this, i, options.toBundle());
                    break;

                case 2:
                    // Edit recordings
                    i = new Intent(MainActivity.this,MainActivity.class);
                    i.putExtra(EXTRA_TITLES_ID,R.array.edit_recording);
                    i.putExtra(EXTRA_ICONS_ID,R.array.edit_recording_icons);
                    i.putExtra(EXTRA_PAGE_ID,R.string.edit_recording_title);
                    i.putExtra(EXTRA_TITLE_ITEM_TEXT,item.getTitle());
                    i.putExtra(EXTRA_TITLE_ITEM_ICON,item.getIcon());
                    i.putExtra(EXTRA_TITLE_ITEM_STATE,item.isComplete());
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, new Pair<>(view, getString(R.string.transition_name_listitem)));
                    ActivityCompat.startActivity(MainActivity.this, i, options.toBundle());
                    break;
            }
        }
    };

    private IRecyclerViewItemClickedListener editListenersTitle = new IRecyclerViewItemClickedListener() {
        @Override
        public void recyclerViewItemClicked(View view, int position) {
            Intent i = null;

            CheckListItem item = adapter.getItem(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                view.setTransitionName(getString(R.string.transition_name_listitem));

            switch (position) {
                case 0:
                    i = new Intent(MainActivity.this,NumberInputActivity.class);
                    i.putExtra(NumberInputActivity.EXTRA_TEXT,R.string.number_input_enter_listener_number);
                    i.putExtra(NumberInputActivity.EXTRA_MODE,NumberInputActivity.InputMode.ADD_LISTENER.ordinal());
                    i.putExtra(EXTRA_TITLE_ITEM_TEXT,item.getTitle());
                    i.putExtra(EXTRA_TITLE_ITEM_ICON,item.getIcon());
                    i.putExtra(EXTRA_TITLE_ITEM_STATE,item.isComplete());
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, new Pair<>(view, getString(R.string.transition_name_listitem)));
                    ActivityCompat.startActivityForResult(MainActivity.this,i,NumberInputActivity.REQUEST_CODE,options.toBundle());
                    break;

                case 1:
                    i = new Intent(MainActivity.this, ShowListenersActivity.class);
                    startActivity(i);
                    break;

            }
        }
    };

    private IRecyclerViewItemClickedListener editRecordingTitle = new IRecyclerViewItemClickedListener() {
        @Override
        public void recyclerViewItemClicked(View view, int position) {
            Intent i =  null;

            CheckListItem item = adapter.getItem(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                view.setTransitionName(getString(R.string.transition_name_listitem));

            switch (position) {

                // Edit Trailers
                case 0:

                    i = new Intent(MainActivity.this,MainActivity.class);
                    i.putExtra(EXTRA_TITLES_ID,R.array.create_trailer);
                    i.putExtra(EXTRA_ICONS_ID,R.array.edit_trailer_icons);
                    i.putExtra(EXTRA_PAGE_ID,R.string.create_trailer_title);
                    i.putExtra(EXTRA_TITLE_ITEM_TEXT,item.getTitle());
                    i.putExtra(EXTRA_TITLE_ITEM_ICON,item.getIcon());
                    i.putExtra(EXTRA_TITLE_ITEM_STATE,item.isComplete());
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, new Pair<>(view, getString(R.string.transition_name_listitem)));
                    ActivityCompat.startActivity(MainActivity.this, i, options.toBundle());

                    break;

                // Edit contents
                case 1:

                    i = new Intent(MainActivity.this,MainActivity.class);
                    i.putExtra(EXTRA_TITLES_ID,R.array.edit_contents_array);
                    i.putExtra(EXTRA_ICONS_ID,R.array.edit_contents_icons);
                    i.putExtra(EXTRA_PAGE_ID,R.string.edit_content_title);
                    i.putExtra(EXTRA_TITLE_ITEM_TEXT,item.getTitle());
                    i.putExtra(EXTRA_TITLE_ITEM_ICON,item.getIcon());
                    i.putExtra(EXTRA_TITLE_ITEM_STATE,item.isComplete());
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, new Pair<>(view, getString(R.string.transition_name_listitem)));
                    ActivityCompat.startActivity(MainActivity.this, i, options.toBundle());

                    break;

            }
        }
    };

    private IRecyclerViewItemClickedListener createTrailerTitle = new IRecyclerViewItemClickedListener() {
        @Override
        public void recyclerViewItemClicked(View view, int position) {
            Intent i =  null;

            CheckListItem item = adapter.getItem(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                view.setTransitionName(getString(R.string.transition_name_listitem));

            switch (position) {

                //create trailer
                case 0:

                    uploadTrailer = true;

                    Intent fileExploreIntent = new Intent(FileBrowserActivity.INTENT_ACTION_SELECT_FILE,
                            null,
                            getApplicationContext(),
                            FileBrowserActivity.class
                    );

                    startActivityForResult(
                            fileExploreIntent,
                            REQUEST_CODE_PICK_FILE
                    );

                    break;

                //play trailer
                case 1:


                    AlertDialog.Builder playTrailerAlertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    playTrailerAlertDialogBuilder.setMessage(getResources().getString(R.string.dialog_call_waiting_listen_trailer));
                    playTrailerAlertDialogBuilder.setCancelable(false);
                    alertDialogPlayTrailer = playTrailerAlertDialogBuilder.create();
                    alertDialogPlayTrailer.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(TWENTY_SECOND_CLOCK);

                                if(alertDialogPlayTrailer != null) {
                                    if(alertDialogPlayTrailer.isShowing()) {
                                        alertDialogPlayTrailer.dismiss();
                                    }
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    FreeSwitchApi.shared().playTrailer(new IMessageListener() {
                        @Override
                        public void success() {


                        }

                        @Override
                        public void fail() {
                            alertDialogPlayTrailer.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(R.string.dialog_create_trailer_first);
                            builder.setNegativeButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.create().show();

                        }

                        @Override
                        public void error() {

                        }

                        @Override
                        public void message(String message) {

                        }
                    });

                    break;

                //delete trailer
                case 2:

                    FreeSwitchApi.shared().deleteTrailer(new IMessageListener() {
                        @Override
                        public void success() {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_trailer_deleted), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void fail() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(R.string.dialog_create_trailer_first);
                            builder.setNegativeButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.create().show();
                        }

                        @Override
                        public void error() {

                        }

                        @Override
                        public void message(String message) {

                        }
                    });

                    break;
            }
        }
    };

    private IRecyclerViewItemClickedListener editContentsTitle = new IRecyclerViewItemClickedListener() {
        @Override
        public void recyclerViewItemClicked(View view, int position) {
            Intent i =  null;

            CheckListItem item = adapter.getItem(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                view.setTransitionName(getString(R.string.transition_name_listitem));

            switch (position) {

                //upload content
                case 0:

                    uploadTrailer = false;

                    Intent fileExploreIntent = new Intent(FileBrowserActivity.INTENT_ACTION_SELECT_FILE,
                            null,
                            getApplicationContext(),
                            FileBrowserActivity.class
                    );

                    startActivityForResult(
                            fileExploreIntent,
                            REQUEST_CODE_PICK_FILE
                    );

                    break;

                //play content
                case 1:

                    AlertDialog.Builder playTrailerAlertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    playTrailerAlertDialogBuilder.setMessage(getResources().getString(R.string.dialog_call_waiting_listen_trailer));
                    playTrailerAlertDialogBuilder.setCancelable(false);
                    alertDialogPlayTrailer = playTrailerAlertDialogBuilder.create();
                    alertDialogPlayTrailer.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(TWENTY_SECOND_CLOCK);

                                if(alertDialogPlayTrailer != null) {
                                    if(alertDialogPlayTrailer.isShowing()) {
                                        alertDialogPlayTrailer.dismiss();
                                    }
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    FreeSwitchApi.shared().playContent(new IMessageListener() {
                        @Override
                        public void success() {

                        }

                        @Override
                        public void fail() {
                            alertDialogPlayTrailer.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(R.string.dialog_create_content_first);
                            builder.setNegativeButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.create().show();

                        }

                        @Override
                        public void error() {

                        }

                        @Override
                        public void message(String message) {

                        }
                    });

                    break;

                //delete content
                case 2:

                    FreeSwitchApi.shared().deleteContent(new IMessageListener() {
                        @Override
                        public void success() {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_trailer_deleted), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void fail() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle(R.string.dialog_create_content_first);
                            builder.setNegativeButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.create().show();
                        }

                        @Override
                        public void error() {

                        }

                        @Override
                        public void message(String message) {

                        }
                    });

                    break;
            }
        }
    };

    private IRecyclerViewItemClickedListener editGuestTitle = new IRecyclerViewItemClickedListener() {
        @Override
        public void recyclerViewItemClicked(View view, int position) {
            Intent i = null;

            CheckListItem item = adapter.getItem(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                view.setTransitionName(getString(R.string.transition_name_listitem));

            switch (position) {
                case 0:

                    // Add guest
                    i = new Intent(MainActivity.this,NumberInputActivity.class);
                    i.putExtra(NumberInputActivity.EXTRA_TEXT,R.string.number_input_enter_guest_number);
                    i.putExtra(NumberInputActivity.EXTRA_MODE,NumberInputActivity.InputMode.ADD_GUEST.ordinal());
                    i.putExtra(EXTRA_TITLE_ITEM_TEXT,item.getTitle());
                    i.putExtra(EXTRA_TITLE_ITEM_ICON,item.getIcon());
                    i.putExtra(EXTRA_TITLE_ITEM_STATE,item.isComplete());
                    i.putExtra("RADIO",false);
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, new Pair<>(view, getString(R.string.transition_name_listitem)));
                    ActivityCompat.startActivityForResult(MainActivity.this,i,NumberInputActivity.REQUEST_CODE,options.toBundle());

                    break;

                case 1:

                    // show guests
                    i = new Intent(MainActivity.this, ShowGuestsActivity.class);
                    startActivity(i);
                    break;

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_FILE) {
            if(resultCode == RESULT_OK) {
                String newFile = data.getStringExtra(
                        FileBrowserActivity.returnFileParameter);

                File fileToUpload = new File(newFile);
                if(fileToUpload.exists()) {

                    String fileKey = "";
                    if(uploadTrailer) {
                        fileKey = GlobalUtils.shared().getStudioId()+"/trailer/"+fileToUpload.getName();
                    }
                    else {
                        fileKey = GlobalUtils.shared().getStudioId()+"/content/"+fileToUpload.getName();
                    }

                    AWSHandler.shared().storeAwsFile(fileToUpload, fileKey, MainActivity.this);
                }
                else {
                    Log.v("dks","file not found");
                }

            } else {
                Toast.makeText(MainActivity.this, R.string.toast_fail, Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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
                ShowListenersActivity.parse(message);

            }
        });
    }

    private void getGuests() {
        FreeSwitchApi.shared().showGuests(new IMessageListener() {
            @Override
            public void success() {

            }

            @Override
            public void fail() {

            }

            @Override
            public void error() {

            }

            @Override
            public void message(String message) {
                parse(message);
            }
        });
    }

    public void parse(String message) {
        String[] guestNames = message.split(",");

        if(ShowGuestsActivity.guestArrayList == null)
            ShowGuestsActivity.guestArrayList = new ArrayList<>();
        ShowGuestsActivity.guestArrayList.clear();

        if(!(guestNames.length == 1 && guestNames[0].equals(""))) {
            for(String names: guestNames) {
                if(names.equalsIgnoreCase("") || names.equalsIgnoreCase(" ")) {
                    continue;
                }
                ShowGuestsActivity.guestArrayList.add(names);
            }
        }

        sleepThread.interrupt();
    }

    @Override
    public void onBackPressed() {

        // if fragment is not present in backstack then exit using finishActivity
        // or just pop the fragment and display previous fragment
        if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }
        else {
            super.onBackPressed();
        }
    }
}
