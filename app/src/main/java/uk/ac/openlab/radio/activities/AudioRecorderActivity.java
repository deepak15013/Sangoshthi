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

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import uk.ac.openlab.radio.BuildConfig;
import uk.ac.openlab.radio.GlobalUtils;
import uk.ac.openlab.radio.R;
import uk.ac.openlab.radio.datatypes.AudioRecording;
import uk.ac.openlab.radio.drawables.RecordingView;
import uk.ac.openlab.radio.network.FreeSwitchApi;
import uk.ac.openlab.radio.network.IMessageListener;

/**
 * Created by Kyle Montague on 26/02/16.
 */
public class AudioRecorderActivity extends AppCompatActivity {

    boolean localCapture = false;//todo change this to use the build variant instead



    Toolbar toolbar;
    RecordingView recordingView;
    Button record;
    Button playback;
    Button delete;
    Button finish;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    String directory;
    AudioRecording recording;

    private final String fileFormat = ".aac";
    private boolean isRecording = false;


    private FreeSwitchApi freeSwitchApi;


    public static final int REQUEST_CODE = 9978;
    public static final String EXTRA_IS_LOCAL_CAPTURE = "EXTRA.LOCAL_CAPTURE";
    public static final String EXTRA_AUDIO = "EXTRA.AUDIO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(GlobalUtils.appTheme());
        setContentView(R.layout.activity_audio_recorder);

        // Set a toolbar to replace the action bar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        freeSwitchApi = FreeSwitchApi.shared();

        record = (Button)findViewById(R.id.button_record);
        playback = (Button)findViewById(R.id.button_playback);
        delete = (Button)findViewById(R.id.button_delete);
        finish = (Button)findViewById(R.id.button_finish);
        recordingView = (RecordingView)findViewById(R.id.canvas);

        //setup click listeners
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording){
                    stopRecording();
                    isRecording = false;
                }else {
                    recordAudio();
                    isRecording = true;
                }
            }
        });
        playback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayback();
                stopPlayback();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAudio();
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });

        recording = new AudioRecording();

        // Local capture not used in this flavor of the app - handled by IVR
        if(BuildConfig.FLAVOR.equals("citizen"))
            localCapture = false;


        if(localCapture){
            //init audio recorder.
            mediaRecorder = new MediaRecorder();


            //init audio player
            mediaPlayer = new MediaPlayer();
            directory = Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name)+"/"+getString(R.string.Directory_Audio);

            File dir = new File(directory);
            if(!dir.exists())
                dir.mkdirs();

        }
    }



    private void done() {
        Intent returnIntent = new Intent();
        if(localCapture){
            mediaRecorder.release();
            mediaPlayer.release();
        }else{
            //todo release the connection with IVR
        }

        if(recording.getUri() != null) {
            returnIntent.putExtra(EXTRA_IS_LOCAL_CAPTURE, localCapture);
            returnIntent.putExtra(EXTRA_AUDIO, recording);
            setResult(Activity.RESULT_OK,returnIntent);
        }else
            setResult(Activity.RESULT_CANCELED);

        finishActivity(REQUEST_CODE);
    }

    private void deleteAudio() {
        if(localCapture){
            GlobalUtils.DeleteRecursive(recording.getUri());
            recording.setUri(null);
            updateInterface(state.NO_RECORDING);


        }else{
            //todo tell the IVR to delete the clip with id <audio_clip_id>
            freeSwitchApi.deleteRecording(new IMessageListener() {
                @Override
                public void success() {
                    updateInterface(state.NO_RECORDING);
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
            },null);

        }
    }

    private void stopPlayback() {
        if(localCapture){
            try {
                mediaPlayer.stop();
                updateInterface(state.HAS_RECORDING);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            freeSwitchApi.stopRecordingPlayback(new IMessageListener() {
                @Override
                public void success() {
                    updateInterface(state.HAS_RECORDING);
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
        }
    }


    private void startPlayback() {
        if(localCapture){
            //todo playback the local file
            try {
                mediaPlayer.reset();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                File file = new File(recording.getUri());
                if(file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    Log.d("URI", uri.toString());
                    mediaPlayer.setDataSource(getApplicationContext(), uri);
                    mediaPlayer.prepare();
                    recordingView.setMaxDuration(mediaPlayer.getDuration()/1000);
                    updateInterface(state.IS_PLAYING);
                    mediaPlayer.start();

                }
            }catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            //todo I need to know when the audio recording finishes / how long it will last so i can animate this when playing back.
            freeSwitchApi.startRecordingPlayback(new IMessageListener() {
                @Override
                public void success() {
                    updateInterface(state.IS_PLAYING);
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
        }
    }

    int recordingDuration = 30; //todo change this from hardcoding to using the template structure.

    private void recordAudio() {

            recordingView.setMaxDuration(recordingDuration);//todo need to pull this from somewhere (i.e. strings/ integers file)
        if(localCapture){
            recording.setUri(directory+String.format("audio_%d%s", System.currentTimeMillis(),fileFormat));

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(recording.getUri());
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();

//              //UI Changes
                updateInterface(state.IS_RECORDING);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            freeSwitchApi.startRecording(recordingDuration, new IMessageListener() {
                @Override
                public void success() {
                    Toast.makeText(getApplicationContext(),"SUCCESS",Toast.LENGTH_SHORT).show();
                    updateInterface(state.IS_RECORDING);
                }

                @Override
                public void fail() {
                    Toast.makeText(getApplicationContext(),"SUCCESS",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void error() {
                    Toast.makeText(getApplicationContext(),"SUCCESS",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void message(String message) {

                }
            });
        }
    }


    private void stopRecording(){
        if(localCapture){
            mediaRecorder.stop();
            mediaRecorder.reset();
            updateInterface(state.HAS_RECORDING);

        }else{
            freeSwitchApi.stopRecording(new IMessageListener() {
                @Override
                public void success() {
                    updateInterface(state.HAS_RECORDING);
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
        }


    }


    private enum state{
        NO_RECORDING,
        IS_RECORDING,
        HAS_RECORDING,
        IS_PLAYING
    }

    private void updateInterface(state state){
        switch (state){
            case NO_RECORDING:
                record.setText(R.string.audio_recorder_record_button);
                record.setVisibility(View.VISIBLE);
                playback.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                recordingView.setTicker(0);
                recordingView.stop();
                break;
            case IS_RECORDING:
                recordingView.setTicker(0);
                recordingView.start();
                record.setText("Stop");
                record.setVisibility(View.VISIBLE);
                playback.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                break;
            case HAS_RECORDING:
                recordingView.setTicker(0);
                recordingView.stop();
                record.setVisibility(View.GONE);
                playback.setVisibility(View.VISIBLE);
                playback.setText("Play");
                delete.setVisibility(View.VISIBLE);
                break;
            case IS_PLAYING:
                recordingView.setTicker(0);
                recordingView.start();
                record.setVisibility(View.GONE);
                playback.setVisibility(View.VISIBLE);
                playback.setText("Stop");
                delete.setVisibility(View.GONE);
                break;
        }

    }


}
