package com.example.user.weshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener {

    private CallbackManager callbackManager;

    private AccessToken accessToken;
    private int idcount=0;
    private ArrayList <friend> friendlist = new ArrayList();
    private ArrayList<videolist> vdolist = new ArrayList();
    public String userid= new String();
    public String username= new String();
    public  boolean token ;
    int length ;
    public String asd = "1227051150653960";
    private Thread thread;







    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.maps);


        callbackManager = CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager, callback);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "lh1xjhuVBGQxdPWjMR2YatrCS2Ez0cszhebl9PTs", "Iz3blIeSqdtXAYnBCQGimuBL3pJFntUZhsoheXLV");

        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);                         //顯示目前位置
        mMap.getUiSettings().setCompassEnabled(true);           //顯示指南針
        //mMap.getUiSettings().setZoomControlsEnabled(true);      //顯示縮放圖示
        mMap.getUiSettings().setRotateGesturesEnabled(true);    //可手勢控制地圖旋轉
        mMap.getUiSettings().setTiltGesturesEnabled(true);      //可手勢調整俯視角度*/
        mMap.setOnMarkerClickListener(this);


        token=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if(mMap!=null){
            mMap.clear();
        }
    }





    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {


        }
    public void toFilm(View v){
        Intent intent = new Intent();
        intent.setClass(MapsActivity.this, FilmActivity.class);
        int requestCode = 111;
        intent.putExtra("uid", userid);
        intent.putExtra("uname", username);
        startActivityForResult(intent, requestCode);
        //MapsActivity.this.finish();
    }


    public void toTest(View view){
        mMap.clear();
        Log.d("DebugLog", "go to test");
        for (int i =0;i<vdolist.size();i++)
        {

            MarkerOptions markerOpt = new MarkerOptions();
            markerOpt.position(new LatLng(vdolist.get(i).getLat(), vdolist.get(i).getLng()));
            markerOpt.title(vdolist.get(i).getName());
            markerOpt.snippet(vdolist.get(i).getVid());
            markerOpt.draggable(false);
            markerOpt.visible(true);
            if (vdolist.get(i).getPriority().equals(0)) {
                markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin65_red));
            } else {
                markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin65_32));
            }

            Marker marker = mMap.addMarker(markerOpt);
            //marker.showInfoWindow();
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {
                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                    // Getting the position from the marker
                    LatLng latLng = arg0.getPosition();

                    // Getting reference to the TextView to set latitude
                    ImageView imageView = (ImageView) v.findViewById(R.id.imageView3);
                    imageView.setImageResource(R.drawable.key3);
                    String imageURL;
                    imageURL = "https://graph.facebook.com/" + arg0.getSnippet() + "/picture";
                    Log.d("DebugLog", "" + imageURL);

                    //Picasso.with(getApplicationContext()).load(imageURL).into(imageView);
                    // Setting the latitude


                    return v;

                }
            });
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.d("DebugLog", "try");
                    for (int i = 0; i<vdolist.size();i++) {
                        Log.d("DebugLog", "title: "+marker.getSnippet());
                        Log.d("DebugLog", "vidd: " + vdolist.get(i).getVid());
                        if (marker.getSnippet().equals(vdolist.get(i).getVid())) {
                            Log.d("DebugLog", "tttt");
                            String url = vdolist.get(i).getFile().getUrl();
                            Intent intent = new Intent();
                            intent.setClass(MapsActivity.this, CheckKey.class);
                            intent.putExtra("vid", vdolist.get(i).getVid());
                            Log.d("DebugLog", "vidd: " + vdolist.get(i).getVid());
                            intent.putExtra("uid", vdolist.get(i).getId());
                            Log.d("DebugLog", "vidd: " + vdolist.get(i).getId());
                            intent.putExtra("uname", vdolist.get(i).getName());
                            Log.d("DebugLog", "vidd: " + vdolist.get(i).getName());
                            intent.putExtra("lat", vdolist.get(i).getLat());
                            intent.putExtra("lng", vdolist.get(i).getLng());
                            intent.putExtra("file",url);
                            Log.d("DebugLog", "vidd: " + url);
                            intent.putExtra("message", vdolist.get(i).getMessage());
                            Log.d("DebugLog", "vidd: " + vdolist.get(i).getMessage());
                            startActivity(intent);
                            break;

                        }
                    }


                }
            });

        }




    }
    public void toTest2(){
        Log.d("DebugLog", "go to test2");
        for (int i =0;i<vdolist.size();i++)
        {

            MarkerOptions markerOpt = new MarkerOptions();
            markerOpt.position(new LatLng(vdolist.get(i).getLat(), vdolist.get(i).getLng()));
            markerOpt.title(vdolist.get(i).getName());
            markerOpt.snippet(vdolist.get(i).getVid());
            markerOpt.draggable(false);
            markerOpt.visible(true);
            if (vdolist.get(i).getPriority().equals(0)) {
                markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin65_red));
            } else {
                markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin65_32));
            }

            Marker marker = mMap.addMarker(markerOpt);
            //marker.showInfoWindow();
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker arg0) {
                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                    // Getting the position from the marker
                    LatLng latLng = arg0.getPosition();

                    // Getting reference to the TextView to set latitude
                    ImageView imageView = (ImageView) v.findViewById(R.id.imageView3);
                    imageView.setImageResource(R.drawable.key3);
                    String imageURL;
                    imageURL = "https://graph.facebook.com/" + arg0.getSnippet() + "/picture";
                    Log.d("DebugLog", "" + imageURL);

                    //Picasso.with(getApplicationContext()).load(imageURL).into(imageView);
                    // Setting the latitude


                    return v;

                }
            });
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.d("DebugLog", "try");
                    for (int i = 0; i<vdolist.size();i++) {
                        Log.d("DebugLog", "title: "+marker.getSnippet());
                        Log.d("DebugLog", "vidd: " + vdolist.get(i).getVid());
                        if (marker.getSnippet().equals(vdolist.get(i).getVid())) {
                            Log.d("DebugLog", "tttt");
                            String url = vdolist.get(i).getFile().getUrl();
                            Intent intent = new Intent();
                            intent.setClass(MapsActivity.this, CheckKey.class);
                            intent.putExtra("vid", vdolist.get(i).getVid());
                            Log.d("DebugLog", "vidd: " + vdolist.get(i).getVid());
                            intent.putExtra("uid", vdolist.get(i).getId());
                            Log.d("DebugLog", "vidd: " + vdolist.get(i).getId());
                            intent.putExtra("uname", vdolist.get(i).getName());
                            Log.d("DebugLog", "vidd: " + vdolist.get(i).getName());
                            intent.putExtra("lat", vdolist.get(i).getLat());
                            intent.putExtra("lng", vdolist.get(i).getLng());
                            intent.putExtra("file",url);
                            Log.d("DebugLog", "vidd: " + url);
                            intent.putExtra("message", vdolist.get(i).getMessage());
                            Log.d("DebugLog", "vidd: " + vdolist.get(i).getMessage());
                            startActivity(intent);
                            break;

                        }
                    }


                }
            });

        }




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
        if (requestCode==111)
        {
            if(mMap!=null)
                mMap.clear();

            myThreadTask mytask=new myThreadTask();
            mytask.setTask(new task() {
                @Override
                public void doit() {
                    //要在背後做得是
                    setVideo();

                }
            });
            mytask.setCallback(new GetUserCallback() {
                @Override
                public void done(Intent intent) {
                    //要傳參數用這個
                }

                @Override
                public void done() {
                    //做完要做的是
                    //toTest2();
                }
            });
            mytask.execute();
        }
        else
            callbackManager.onActivityResult(requestCode, resultCode, data);

    }
    public void setVideo(){
        //取Vlist資料
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Vlist");
        query.whereEqualTo("priority", 1);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            ParseObject p = list.get(i);
                            String id = p.getString("uid");
                            String name = p.getString("uname");
                            String vid = p.getString("vid");
                            Double lat = Double.parseDouble(p.getNumber("lat").toString());
                            Double lng = Double.parseDouble(p.getNumber("lng").toString());
                            String message = p.getString("message");
                            Number priority = p.getNumber("priority");
                            ParseFile file = p.getParseFile("key");
                            Log.d("DebugLog", "p'sid:" + id);

                            for (int a = 0; a < friendlist.size(); a++) {
                                Log.d("DebugLog", "f'sid:" + friendlist.get(a).getId());

                                if (friendlist.get(a).getId().equals(id)) {
                                    videolist vdolistobj = new videolist();
                                    vdolistobj.setId(id);
                                    vdolistobj.setName(name);
                                    vdolistobj.setVid(vid);
                                    vdolistobj.setLat(lat);
                                    vdolistobj.setLng(lng);
                                    vdolistobj.setMessage(message);
                                    vdolistobj.setPriority(priority);
                                    vdolistobj.setFile(file);

                                    Log.d("DebugLog", "第" + i + "以放入");
                                    vdolist.add(vdolistobj);

                                } else {
                                    Log.d("DebugLog", "f'sid:faile");
                                }
                            }

                        }

                    }
                }
            }

        });



        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Vlist");
        query1.whereEqualTo("priority", 0);

        query1.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            ParseObject p = list.get(i);
                            String id = p.getString("uid");
                            String name = p.getString("uname");
                            String vid = p.getString("vid");
                            Double lat = Double.parseDouble(p.getNumber("lat").toString());
                            Double lng = Double.parseDouble(p.getNumber("lng").toString());
                            String message = p.getString("message");
                            Number priority = p.getNumber("priority");
                            ParseFile file = p.getParseFile("key");
                            Log.d("DebugLog", "p'sid:" + id);


                            videolist vdolistobj = new videolist();
                            vdolistobj.setId(id);
                            vdolistobj.setName(name);
                            vdolistobj.setVid(vid);
                            vdolistobj.setLat(lat);
                            vdolistobj.setLng(lng);
                            vdolistobj.setMessage(message);
                            vdolistobj.setPriority(priority);
                            vdolistobj.setFile(file);

                            Log.d("DebugLog", "第" + i + "以放入");
                            vdolist.add(vdolistobj);

                        }
                    }
                }
            }

        });
    }

    public void getUserFriend() {
        new GraphRequest(accessToken.getCurrentAccessToken(),"/me/friends",null,HttpMethod.GET,new GraphRequest.Callback() {
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
                                Log.d("DebugLog",""+friendlist.get(i).getId());

                            }



                        } catch (JSONException e) {
                            Log.d("DebugLog", "Freind Get Failed!");
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
        this.setVideo();

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
                            friend friend = new friend();
                            friend.setId(userid);
                            friend.setName(username);
                            friendlist.add(friend);
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("User_id");
                            query.whereEqualTo("user_uid", jsonProfile.getString("id"));
                            query.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> User_id, ParseException e) {
                                    if (User_id.size() != 0) {
                                        Log.d("DebugLog", "existed user");
                                    } else {
                                        ParseObject putusertoparse = new ParseObject("User_id");
                                        putusertoparse.put("user_uid", userid);
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


    @Override
    public boolean onMarkerClick(Marker marker) {
        /*Log.d("DebugLog","try");
        for (int i = 0; i<vdolist.size();i++) {
            Log.d("DebugLog", "title: "+marker.getTitle());
            Log.d("DebugLog", "vidd: " + vdolist.get(i).getVid());
            if (marker.getTitle().equals(vdolist.get(i).getVid())) {
                Log.d("DebugLog", "tttt");
                String url = vdolist.get(i).getFile().getUrl();
                Intent intent = new Intent();
                intent.setClass(MapsActivity.this, CheckKey.class);
                intent.putExtra("vid", vdolist.get(i).getVid());
                Log.d("DebugLog", "vidd: " + vdolist.get(i).getVid());
                intent.putExtra("uid", vdolist.get(i).getId());
                Log.d("DebugLog", "vidd: " + vdolist.get(i).getId());
                intent.putExtra("uname", vdolist.get(i).getName());
                Log.d("DebugLog", "vidd: " + vdolist.get(i).getName());
                intent.putExtra("lat", vdolist.get(i).getLat());
                intent.putExtra("lng", vdolist.get(i).getLng());
                intent.putExtra("file",url);
                Log.d("DebugLog", "vidd: " + url);
                intent.putExtra("message", vdolist.get(i).getMessage());
                Log.d("DebugLog", "vidd: " + vdolist.get(i).getMessage());
                startActivity(intent);
                finish();
                break;

            }
        }
*/
        marker.showInfoWindow();
        return false;
    }

}
