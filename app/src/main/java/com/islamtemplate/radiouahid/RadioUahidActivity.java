package com.islamtemplate.radiouahid;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;


public class RadioUahidActivity extends Activity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener {

    public static final String RADIO_URL = "http://174.36.1.92:5659/Live";

    MediaPlayer radioUahidPlayer;
    TextView titleTextView;
    TextView playingTextView;
    Button playPauseButton;
    Button stopButton;

    ProgressDialog progressDialog;

    boolean isStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideActionbar();

        setContentView(R.layout.activity_radio_uahid);

        titleTextView = (TextView) findViewById(R.id.title_textview);
        playingTextView = (TextView) findViewById(R.id.playing_textview);
        playPauseButton = (Button) findViewById(R.id.play_button);
        stopButton = (Button) findViewById(R.id.stop_button);

        isStopped = true;
        titleTextView.setText("Laden...");

        radioUahidPlayer = new MediaPlayer();
        radioUahidPlayer.setOnPreparedListener(this);
        radioUahidPlayer.setOnBufferingUpdateListener(this);
        initializePlayer();
        setButtonListeners();


    }

    private void hideActionbar() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }

    private void initializePlayer() {

            progressDialog = new ProgressDialog(RadioUahidActivity.this);
            progressDialog.setMessage("Lade Stream...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);

        Runnable initRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    radioUahidPlayer.setDataSource(RADIO_URL);
                    radioUahidPlayer.prepare();
                    radioUahidPlayer.start();
                    isStopped = false;

                    radioUahidPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread initThread = new Thread(initRunnable);
        initThread.start();

    }

    private void setButtonListeners() {
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioUahidPlayer.isPlaying()) {
                    radioUahidPlayer.pause();
                } else if (isStopped){
                    initializePlayer();
                } else {
                    radioUahidPlayer.start();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioUahidPlayer != null) {
                    radioUahidPlayer.stop();
                    radioUahidPlayer.reset();
                    isStopped = true;

                }
            }
        });
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        titleTextView.setText("Es läuft gerade");
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(RADIO_URL, new HashMap<String, String>());
        String artist =  metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        playingTextView.setText(artist + " - " + title);
    }
}
