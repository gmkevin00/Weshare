package com.example.user.weshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckKey extends AppCompatActivity implements LocationListener {
    public String vid;
    public String uid;
    public String uname;
    public Double lat;
    public Double lng;
    public String message;
    public String file;
    private LocationManager mgr;
    public double nowlat;
    public double nowlng;
    private static final int REQUEST_CAPTURE_KEY = 100;
    Bitmap Source_bmp;
    Bitmap Target_bmp;
    public Uri uuu;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Log.d("DebugLog",file);
        setContentView(R.layout.activity_check_key);


        mgr = (LocationManager)getSystemService(LOCATION_SERVICE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("計算中");
        progressDialog.setMessage("請稍候....");


        set1();








    }
    public void set1(){
        TextView textView =(TextView)findViewById(R.id.textView4);
        Log.d("DebugLog","set1");
        Bundle extras = getIntent().getExtras();

        vid = extras.getString("vid");
        Log.d("DebugLog",vid);
        uid = extras.getString("uid");
        Log.d("DebugLog",uid);
        uname = extras.getString("uname");
        textView.setText(uname+"的貼文");
        Log.d("DebugLog",uname);
        message =  extras.getString("message");
        Log.d("DebugLog",message);
        file = extras.getString("file");
        myThreadTask getBitMapFromUrlTask=new myThreadTask();
        getBitMapFromUrlTask.setTask(new task() {
            @Override
            public void doit() {
                Target_bmp = getBitmapFromURL(file);
            }
        });
        getBitMapFromUrlTask.setCallback(new GetUserCallback() {
            @Override
            public void done(Intent intent) {

            }

            @Override
            public void done() {
                ;
            }
        });
        getBitMapFromUrlTask.execute();






        Log.d("DebugLog",file);
        lat = extras.getDouble("lat");
        Log.d("DebugLog", "" + lat);
        lng = extras.getDouble("lng");
        Log.d("DebugLog", "" + lng);
        set2();
    }
    public void set2(){
        Log.d("DebugLog","set2");

        //TextView t2 = (TextView) findViewById(R.id.textView3);
        //TextView t3 = (TextView) findViewById(R.id.textView4);
        ImageView imageView = (ImageView) findViewById(R.id.imageView3);
        Log.d("DebugLog", "hit");
        Picasso.with(imageView.getContext()).load(file).into(imageView);
        //Target_bmp = getBitmapFromURL(file);
        Log.d("DebugLog", "uname");
        Log.d("DebugLog", "message");
        //t2.setText(uname);
        //t3.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_key, menu);
        return true;
    }
    public void toMap(View view){
        Intent intent = new Intent();
        intent.setClass(CheckKey.this, MapsActivity.class);
        finish();
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


    @Override
    public void onLocationChanged(Location location) {
        String str ="定位提供者:"+location.getProvider();
        str+= String.format("\n緯度:%5f\n經度:%5f",location.getLatitude(),location.getLongitude());
        //Toast.makeText(CheckKey.this, ""+str, Toast.LENGTH_LONG).show();
        //Log.d("DebugLog", "" + str);
        nowlat= location.getLatitude();
        nowlng= location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        String best = mgr.getBestProvider(new Criteria(), true);
        if (best != null){
            mgr.requestLocationUpdates(best,5000,5,this);//5000毫秒 5 M
        }
        else

            Toast.makeText(CheckKey.this, "請開啟GPS定位!", Toast.LENGTH_LONG).show();
    }
    protected void onPause() {
        super.onPause();
        mgr.removeUpdates(this);

    }
    public void onClick(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAPTURE_KEY);


    }
    public void comfirm(){
        TextView tv = (TextView) findViewById(R.id.textView6);


        ImageComparator CPX = new ImageComparator();
        boolean same = CPX.Similar(Target_bmp, Source_bmp);
        int a = CPX.Getdiffs();


        if (same){
            Log.d("DebugLog","success");
            Target_bmp.recycle();
            Source_bmp.recycle();
            tv.setText("Success Diff:" + a);
            Toast.makeText(CheckKey.this, "Success Diff:"+a, Toast.LENGTH_LONG).show();
            //distFrom(lat,lng,nowlat,nowlng)<=250&&
            Intent intent = new Intent();

            intent.setClass(CheckKey.this, WatchVdo.class);
            intent.putExtra("vid", vid);
            intent.putExtra("uname",uname);
            intent.putExtra("message",message);
            startActivity(intent);
            finish();
        }
        else
        {Toast.makeText(CheckKey.this, "Fail Diff:"+a, Toast.LENGTH_LONG).show();
            Source_bmp.recycle();
            tv.setText("Fail Diff:"+a);}
        /*
        if(distFrom(lat,lng,nowlat,nowlng)<=50)
        {Toast.makeText(CheckKey.this, "兩張照片不一樣", Toast.LENGTH_LONG).show();
            Log.d("DebugLog","wrong pic");}
        else
        if(same)
            Toast.makeText(CheckKey.this, "超出距離", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(CheckKey.this, "兩者皆不符", Toast.LENGTH_LONG).show();*/

    }
    public double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (float) (earthRadius * c);
        return dist;
    }
    public static Bitmap getBitmapFromURL(String src) {




        Log.d("DebugLog", "url to bitmap");
        try {
            Log.d("DebugLog","Start");
            URL url = new URL(src);
            Log.d("DebugLog","0");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Log.d("DebugLog", "1");
            connection.setDoInput(true);
            Log.d("DebugLog", "2");
            connection.connect();
            Log.d("DebugLog", "3");
            InputStream input = connection.getInputStream();
            Log.d("DebugLog","4");
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.d("DebugLog","5");
            return myBitmap;
        } catch (IOException e) {
            Log.d("DebugLog","Fail url to bitmap");
            // Log exception
            return null;
        }
    }

    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            uuu = data.getData();
/*
            Intent it = new Intent();
            it.setClass(CheckKey.this, CheckKey2.class);
            Log.d("DebugLog", "1");
            it.setData(u);
            Log.d("DebugLog", "2");
            it.putExtra("uid", uid);
            Log.d("DebugLog", "3");
            it.putExtra("uname", uname);
            Log.d("DebugLog", "4");
            it.putExtra("vid", vid);
            Log.d("DebugLog", "5");
            it.putExtra("message", message);
            Log.d("DebugLog", "6");
            it.putExtra("lat", lat);
            Log.d("DebugLog", "7");
            it.putExtra("lng", lng);
            Log.d("DebugLog", "8");
            it.putExtra("targetUrl", file);
            Log.d("DebugLog", "9");
            startActivity(it);
            Log.d("DebugLog", "to next");
*/
            if (uuu==null)
            {

                Toast.makeText(CheckKey.this, "發生錯誤,請重新拍攝", Toast.LENGTH_LONG).show();
            }
            else {
                myThreadTask mytask = new myThreadTask();
                mytask.setTask(new task() {
                    @Override
                    public void doit() {
                        //要在背後做得是
                        Log.d("DebugLog", "d1");

                        Source_bmp = resizeBitmap(uuu, CheckKey.this);

                        Log.d("DebugLog", "d2");

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

                        comfirm();
                    }
                });
                mytask.execute();

            }



            //comfirm();


        }

    }

    public static Bitmap resizeBitmap(Uri uri, Context context) {
        ContentResolver cr = context.getContentResolver();
        Bitmap bitmap = null;
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true; //只取bitmap的長寬，不取得整張bitmap

        try {
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, option);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        final int NEW_SIZE = 300;
        int width = option.outWidth;
        int height = option.outHeight;
        int scale = 1;
        while (true) {
            if (width / 2 < NEW_SIZE || height / 2 < NEW_SIZE)
                break;
            width /= 2;
            height /= 2;
            scale++;

        }

        option = new BitmapFactory.Options();
        option.inSampleSize = scale;
        //經過resize後才把bitmap整張圖取出來
        try {
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, option);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;

    }



    public Bitmap afterpic (Uri uri){
        Bitmap bitmap =getBitmapFromUri(uri);


//set rotate
        int w = bitmap.getWidth();
        Log.d("DebugLog",""+w);
        int h = bitmap.getHeight();
        Log.d("DebugLog",""+h);
//set final size
        int destWidth = 250;
        int destHeigth = 140;

//set scale size
        float scaleWidth = ((float) destWidth) / w;
        float scaleHeight = ((float) destHeigth) / h;

//set resize
        Matrix mtx = new Matrix();
        mtx.postScale(scaleWidth, scaleHeight);

// Rotating Bitmap
        Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
        return b;

    }
    private Bitmap getBitmapFromUri(Uri uri)
    {
        try
        {
            // 读取uri所在的图片
            Log.d("DebugLog", "to get uri");
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            Log.d("DebugLog", "get bitmap");

            return bitmap;
        }
        catch (Exception e)
        {
            Log.e("[Android]", e.getMessage());
            Log.e("[Android]", "目录为：" + uri);
            e.printStackTrace();
            return null;
        }
    }







}
