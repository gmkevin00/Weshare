package com.example.user.weshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private ArrayList<friend> friendlist = new ArrayList();
    private ArrayList<videolist> vdolist = new ArrayList();
    public String userid= new String();
    public String username= new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        callbackManager = CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager, callback);

        ImageView imageView = (ImageView) findViewById(R.id.imageView4);
        String imageURL;
        imageURL = "https://graph.facebook.com/1227051150653960/picture";
        Picasso.with(Login.this).load(imageURL).into(imageView);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    private FacebookCallback<LoginResult> callback=new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            accessToken = loginResult.getAccessToken();
            Log.d("DebugLog", "Facebook Login success");
            getUserFriend();
            getUserProfile();
        }

        @Override
        public void onCancel() {
            // App code
            Log.d("DebugLog", "Facebook Login cancel");
        }

        @Override
        public void onError(FacebookException exception) {
            // App code
            Log.d("Log", "Facebook Login fail");
        }
    };
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void toTest(View v){
        Intent intent = new Intent();
        intent.setClass(Login.this, MapsActivity.class);

        startActivity(intent);
        finish();


        //MapsActivity.this.finish();
    }
    public void getUserFriend() {
        new GraphRequest(
                accessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        //Log.d("FB","Members: " + response.toString());
                        JSONObject jsonFriend = response.getJSONObject();
                        //Log.d("DebugLog","Members: " + jsonFriend);

                        try {
                            for (int i = 0; i < jsonFriend.getJSONArray("data").length() ; i++) {

                                friend f = new friend();
                                f.setId(jsonFriend.getJSONArray("data").getJSONObject(i).getString("id"));
                                f.setName(jsonFriend.getJSONArray("data").getJSONObject(i).getString("name"));

                                //Log.d("DebugLog", "Friend_id:" + jsonFriend.getJSONArray("data").getJSONObject(i).getString("id"));
                                //Log.d("DebugLog", "Friend_name:" + jsonFriend.getJSONArray("data").getJSONObject(i).getString("name"));
                                friendlist.add(f);
                            }


                        } catch (JSONException e) {
                            Log.d("DebugLog", "Freind Get Failed!");
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    public void getUserProfile() {
        new GraphRequest(
                accessToken.getCurrentAccessToken(),
                "/me/",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject jsonProfile = response.getJSONObject();
                        try {
                            userid = jsonProfile.getString("id");
                            username = jsonProfile.getString("name");
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("User_id");
                            query.whereEqualTo("user_uid", jsonProfile.getString("id"));
                            query.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> User_id, ParseException e) {
                                    if (User_id.size() != 0) {
                                        Log.d("DebugLog", "existed user");
                                    } else {
                                        ParseObject putusertoparse = new ParseObject("User_id");
                                        putusertoparse.put("user_uid",userid );
                                        Log.d("DebugLog", "" + userid);
                                        putusertoparse.put("user_name", username);
                                        Log.d("DebugLog", username);
                                        putusertoparse.saveInBackground();
                                        Log.d("DebugLog", "put into parse successfully");
                                    }
                                }
                            });
                            //Log.d("DebugLog", "" + jsonProfile.getString("id"));
                            //Log.d("DebugLog", jsonProfile.getString("name"));



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }

}
