package com.example.user.weshare;

/**
 * Created by User on 2015/9/24.
 */

import android.os.AsyncTask;

import org.json.JSONArray;

public class myThreadTask extends AsyncTask<Void,Void,JSONArray> {

    private task t;
    private GetUserCallback getUserCallback;


    public void setTask(task t)
    {
        this.t=t;
    }
    public void setCallback(GetUserCallback getUserCallback)
    {
        this.getUserCallback=getUserCallback;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        t.doit();
        return null;
    }

    @Override
    protected void onPostExecute(JSONArray result){
        super.onPostExecute(result);
        getUserCallback.done();
    }
}

