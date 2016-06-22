package com.example.user.weshare;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.net.URL;

/**
 * Created by User on 2015/9/21.
 */
public class TheTask extends AsyncTask  <Void,Void,JSONArray>{

    private GetUserCallback getUserCallback;
    private URL url;
    public TheTask(GetUserCallback getUserCallback)
    {
        this.getUserCallback=getUserCallback;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {

        try {
            url = new URL("myurl");
            //bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        //return null;
    }

    @Override
    protected void onPostExecute(JSONArray result){
        super.onPostExecute(result);
getUserCallback.done();

    }
}
