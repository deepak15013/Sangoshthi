package uk.ac.openlab.radio.adapters;

import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.datatypes.Caller;
import uk.ac.openlab.radio.datatypes.Callers;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

/**
 * Created by deepaksood619 on 17/6/16.
 */
public class CallerListAdapter extends RecyclerView.Adapter<CallerListAdapter.ViewHolder> {

    private List<Callers> callersList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUserPhoneId;
        ImageButton ibMuteUnmute;
        ImageButton ibVote;
        Chronometer cmTalk;

        public ViewHolder(View v) {
            super(v);
            tvUserPhoneId = (TextView) v.findViewById(R.id.tv_user_phone_id);
            ibMuteUnmute = (ImageButton) v.findViewById(R.id.ib_mute_unmute);

            ibVote = (ImageButton) v.findViewById(R.id.ib_vote);
            cmTalk = (Chronometer) v.findViewById(R.id.cm_talk);

        }
    }

    public CallerListAdapter(List<Callers> callersList) {
        this.callersList = callersList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.caller_list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Callers callers = callersList.get(position);
        String phoneNum = callers.getPhone_number();
        holder.tvUserPhoneId.setText(phoneNum.substring(phoneNum.length() - 3));

        holder.ibMuteUnmute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("dks", "clicked phone: "+callers.getPhone_number());

                if(callers.isMute_state()) {

                    FreeSwitchApi.shared().setCallerMute(new IMessageListener() {
                        @Override
                        public void success() {
                            holder.ibMuteUnmute.setImageResource(R.drawable.ic_phone_active);
                            callers.setMute_state(false);

                            holder.cmTalk.setBase(SystemClock.elapsedRealtime());
                            holder.cmTalk.start();
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
                    }, callers.getUuid(),"false");
                }
                else {

                    FreeSwitchApi.shared().setCallerMute(new IMessageListener() {
                        @Override
                        public void success() {
                            holder.ibMuteUnmute.setImageResource(R.drawable.ic_phone_muted);
                            holder.cmTalk.stop();
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
                    }, callers.getUuid(), "true");
                }
            }
        });

        holder.ibVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!callers.isVoteStarted()) {
                    FreeSwitchApi.shared().startListenerRating(new IMessageListener() {
                        @Override
                        public void success() {
                            Log.v("dks","rating started");
                            holder.ibVote.setImageResource(R.drawable.ic_vote_icon_false);
                            callers.setVoteStarted(true);
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
                    }, callers.getPhone_number());
                } else {
                    FreeSwitchApi.shared().stopListenerRating(new IMessageListener() {
                        @Override
                        public void success() {
                            Log.v("dks","rating stopped");
                            holder.ibVote.setImageResource(R.drawable.ic_vote_icon);
                            callers.setVoteStarted(false);
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
                    }, callers.getPhone_number());
                }


            }
        });


        /*holder.btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("dks","clicked: "+callersList.get(position).getPhone_number());

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return callersList.size();
    }
}
