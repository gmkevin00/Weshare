package com.example.user.weshare;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;


public class FilmActivity extends Activity {
    List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE_UPLOAD);
    private GoogleAccountCredential credential;

    private Uri uri;

    static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    static final int REQUEST_AUTHORIZATION = 1;
    static final int REQUEST_ACCOUNT_PICKER = 2;

    private static final int REQUEST_RECORD_VIDEO = 0;
    private TextView tvMessage;
    private VideoView videoView;
    public String userid;
    public String username;

    // 檔案名稱
    private String fileName;
    // 照片
    private ImageView picture;

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film);
        Intent it = getIntent();
        userid=it.getStringExtra("uid");
        username=it.getStringExtra("uname");


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("上傳影片中");
        progressDialog.setMessage("請稍候....");

        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setCancelable(false);
        progressDialog2.setTitle("請等待");
        progressDialog2.setMessage("請稍候....");

        videoView = (VideoView) findViewById(R.id.videoView);
        //跳相機
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, REQUEST_RECORD_VIDEO);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_film, menu);

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
        intent.setClass(FilmActivity.this, MapsActivity.class);
        finish();
    }
    public void toGetkey(View v){
        Intent intent=new Intent();
        intent.setClass(FilmActivity.this,Getkey.class);
        int requestCode = 111;
        intent.putExtra("uid", userid);
        intent.putExtra("uname", username);

        startActivityForResult(intent, requestCode);
        finish();
    }
    /*public void toGetkey(View v){
        Intent intent = new Intent();
        intent.setClass(FilmActivity.this,Getkey.class);
        int requestCode = 8;
        startActivityForResult(intent, requestCode);
    //UploadVideo u=new UploadVideo();
    //u.setContext(getBaseContext());
    //u.execute();
        //MapsActivity.this.finish();
    }*/



    //video



    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK){
            switch ((requestCode)){
                case REQUEST_RECORD_VIDEO:
                    progressDialog2.show();
                    uri = intent.getData();
                    //tvMessage.setText(getRealPathFromUri(uri));
                    //MediaController mediaController = new MediaController();
                    Log.d("DebugLog", getRealPathFromUri(uri));
                    videoView.setVideoPath(getRealPathFromUri(uri));
                    videoView.requestFocus();
                    videoView.start();

                    progressDialog2.dismiss();
                    credential = GoogleAccountCredential.usingOAuth2(this, scopes);
                    if (credential.getSelectedAccountName() == null) {
                        chooseAccount();
                    }
                    else
                    {
                        progressDialog.show();
                        uploadYouTubeVideos();
                    }
                    break;
                case REQUEST_AUTHORIZATION:
                    if (resultCode == Activity.RESULT_OK) {
                        progressDialog.show();
                        uploadYouTubeVideos();

                    } else {
                        chooseAccount();
                    }
                    break;
                case REQUEST_ACCOUNT_PICKER:
                    if (resultCode == Activity.RESULT_OK) {
                        String accountName = intent.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                        if (accountName != null) {
                            credential.setSelectedAccountName(accountName);
                            progressDialog.show();
                            uploadYouTubeVideos();

                        }
                    }
                    break;
            }
        }
    }

    private boolean isIntentAvailable(Context context, Intent intent){
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
        return  list.size()>0;
    }

    private String getRealPathFromUri(Uri uri){
        String columns = (MediaStore.Images.Media.DATA);
        Cursor cursor = getContentResolver().query(uri, new String[]{columns}, null, null, null);
        String path = null;
        if(cursor.moveToFirst()){
            path = cursor.getString(0);
            cursor.close();
        }
        return path;
    }


    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }


    public void uploadYouTubeVideos() {
        File file = new File(getRealPathFromUri(uri));


        UploadVideo v=new UploadVideo(new GetUserCallback() {
            @Override
            public void done(Intent intent) {
                startActivityForResult(
                        intent, 1);
            }

            @Override
            public void done() {
                progressDialog.dismiss();
            }
        },this);
        v.setCredential(credential);
        v.setMediaContent(file);
        v.execute();



    }


}
