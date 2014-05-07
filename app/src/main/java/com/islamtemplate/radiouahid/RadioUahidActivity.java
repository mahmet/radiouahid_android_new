package com.islamtemplate.radiouahid;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


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
        playPauseButton.setBackground(getResources().getDrawable(R.drawable.pause_button));
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
                    playPauseButton.setBackground(getResources().getDrawable(R.drawable.play_button));
                } else if (isStopped){
                    initializePlayer();
                    playPauseButton.setBackground(getResources().getDrawable(R.drawable.pause_button));
                } else {
                    radioUahidPlayer.start();
                    playPauseButton.setBackground(getResources().getDrawable(R.drawable.pause_button));
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
                    playPauseButton.setBackground(getResources().getDrawable(R.drawable.play_button));

                }
            }
        });
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        titleTextView.setText("Du hörst gerade");
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}
