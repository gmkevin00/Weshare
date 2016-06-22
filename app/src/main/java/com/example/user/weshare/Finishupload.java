package com.example.user.weshare;

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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Finishupload extends AppCompatActivity implements LocationListener{
    private LocationManager mgr;

    double lat;
    double lng;
    public EditText edtext;
    int priority;
    public String userid;
    public String username;
    public String vid;
    public Uri keyuri;
    ParseFile file;
    public Bitmap bit;
    Intent it;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finishupload);
        mgr = (LocationManager)getSystemService(LOCATION_SERVICE);
        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,this);

        it = getIntent();
        userid=it.getStringExtra("uid");
        username=it.getStringExtra("uname");





        //get vid
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VidList");
        query.orderByDescending("token");
        query.setLimit(1);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("DebugLog", "The getFirst request failed.");
                } else {
                    Log.d("DebugLog", "Retrieved the object.");
                    vid = "" + object.getString("vid");

                }
            }
        });
        Log.d("DebugLog", "in finishupload");


        //Asynk task
        myThreadTask mytask=new myThreadTask();
        mytask.setTask(new task() {
            @Override
            public void doit() {
                //要在背後做得是
                Log.d("DebugLog", "d1");

                keyuri=it.getData();

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
                Bitmap bitmapp;
                bit = resizeBitmap(keyuri, Finishupload.this);





                //bit = afterpic(keyuri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bit.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                file = new ParseFile("try.jpg", byteArray);
                file.saveInBackground();

            }
        });
        mytask.execute();














    }
    @Override
    protected void onResume() {
        super.onResume();
        String best = mgr.getBestProvider(new Criteria(),true);
        if (best != null){
            mgr.requestLocationUpdates(best,5000,5,this);//5000毫秒 5 M
        }
        else

        Toast.makeText(Finishupload.this, "請開啟GPS定位!", Toast.LENGTH_LONG).show();




    }
    protected void onPause() {
        super.onPause();
        mgr.removeUpdates(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_finishupload, menu);
        return true;
    }

    public Bitmap afterpic (Bitmap bitmap){



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






    public void toMap(View view){
        Intent intent = new Intent();
        intent.setClass(Finishupload.this, MapsActivity.class);
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
        //Toast.makeText(Finishupload.this, ""+str, Toast.LENGTH_LONG).show();
        //Log.d("DebugLog", "" + str);
        lat= location.getLatitude();
        lng= location.getLongitude();
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
    public void finish(View v) throws IOException {


        //將key上傳


        //取得權限資訊
        edtext =  (EditText) findViewById(R.id.editText);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rd);
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.topublic:
                priority=0;
                break;
            case R.id.tofriend:
                priority=1;
                break;
        }





        //將所有內容傳至VideoList中
        ParseObject put = new ParseObject("Vlist");
        put.put("uid", userid);                       //使用者id
        Log.d("DebugLog", "" + userid);
        put.put("uname", username);                        //使用者name
        Log.d("DebugLog", "" + username);
        put.put("vid", "" + vid);                          //影片id
        Log.d("DebugLog", vid);
        put.put("lat", lat);                           //所在經度
        Log.d("DebugLog", "" + lat);
        put.put("lng", lng);                          //所在緯度
        Log.d("DebugLog", "" + lng);
        put.put("key", file);                        //愈傳送Key
        put.put("message", "" + edtext.getText());    //愈傳送訊息
        Log.d("DebugLog", "" + edtext.getText());
        put.put("priority", priority);                  //影片之權限
        Log.d("DebugLog", "" + priority);
        put.saveInBackground();
        Log.d("DebugLog", "put into parse successfully");
        //轉換到Map
        Intent intent=new Intent();
        intent.setClass(Finishupload.this,MapsActivity.class);
        finish();
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    private Bitmap getBitmapFromUri(Uri uri)
    {
        try
        {
            // 读取uri所在的图片
            Log.d("DebugLog", "to get uri");
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(Finishupload.this.getContentResolver(), uri);
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
