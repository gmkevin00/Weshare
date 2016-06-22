package com.example.user.weshare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class Getkey extends AppCompatActivity {

    private static final int REQUEST_CAPTURE_KEY = 0;
    private ImageView imageView2;
    public String keypath = "";
    public Uri keyuri;
    public String userid;
    public String username;
    public Uri ur1;
    public ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getkey);

        imageView2 = (ImageView) findViewById(R.id.imageView22);
        Intent it = getIntent();
        userid=it.getStringExtra("uid");
        username=it.getStringExtra("uname");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("處理中");
        progressDialog.setMessage("請稍候....");



        //跳相機
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAPTURE_KEY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_getkey, menu);
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
    public void toMap(View view){
        Intent intent = new Intent();
        intent.setClass(Getkey.this, MapsActivity.class);
        finish();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK){
            switch ((requestCode)){
                case REQUEST_CAPTURE_KEY:


                    Uri uri = intent.getData();

                    keyuri = uri;
                    toFinishupload();
/*
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imageView2.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

*/

                    break;
            }
        }
    }
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("這張照片可以嗎?")
                .setCancelable(false)
                .setPositiveButton("重拍", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Getkey.this.finish();
                    }
                })
                .setNegativeButton("可以", null)
                .show();

    }


    public void toFinishupload(){
        Log.d("debugLog", "1");
        Intent intent = new Intent();
        intent.setClass(Getkey.this, Finishupload.class);
        int requestCode = 111;
        intent.putExtra("keypath", keypath);
        intent.putExtra("uid", userid);
        intent.putExtra("uname", username);
        intent.setData(keyuri);
        startActivityForResult(intent, requestCode);
        finish();
    }
}
