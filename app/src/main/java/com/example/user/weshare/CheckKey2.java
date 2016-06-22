package com.example.user.weshare;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class CheckKey2 extends AppCompatActivity {
    public String vid;
    public String uid;
    public String uname;
    public Double lat;
    public Double lng;
    public String message;
    public String file;
    public String targetUrl;
    public Uri SourceUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_key2);
        set();


    }
    public void set(){
        Log.d("DebugLog", "1");
        Bundle extras = getIntent().getExtras();
        Log.d("DebugLog", "2");
        vid = extras.getString("vid");
        Log.d("DebugLog", "3");
        uid = extras.getString("uid");
        Log.d("DebugLog", "4");
        uname = extras.getString("uname");
        Log.d("DebugLog", "5");
        lat = extras.getDouble("lat");
        Log.d("DebugLog", "6");
        lng=extras.getDouble("lng");
        Log.d("DebugLog", "7");
        message=extras.getString("message");Log.d("DebugLog", "8");
        targetUrl =extras.getString("targetUrl");Log.d("DebugLog", "9");
        SourceUri =getIntent().getData();Log.d("DebugLog", "10");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_key2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
