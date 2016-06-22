package com.example.user.weshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class WatchVdo  extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private Provider provider;
    private YouTubePlayer YPlayer;
    private boolean wasRestored;
    private static final String YoutubeDeveloperKey = "AIzaSyBKfTmagPOnGinksjhPO1EgX7vj6XjFmOw";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    public String vid;
    public String uname;
    public String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_vdo);
        TextView tv= (TextView) findViewById(R.id.textView3);
        TextView textView =(TextView)findViewById(R.id.textView5);

        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(YoutubeDeveloperKey, this);
        Intent intent = getIntent();
        vid =intent.getStringExtra("vid");
        uname =intent.getStringExtra("uname");
        message =intent.getStringExtra("message");
        tv.setText(message);
        textView.setText(uname + "的貼文");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_watch_vdo, menu);
        return true;
    }



    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "There was an error initializing the YouTubePlayer",
                    errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(YoutubeDeveloperKey, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    @Override
    public void onInitializationSuccess(Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        YPlayer = player;


        if (!wasRestored) {


            YPlayer.loadVideo(vid);
            YPlayer.cueVideo(vid);


        }
    }
    public void onBackPressed() {
        onDestroy();
         finish();
    }

}