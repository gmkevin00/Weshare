package com.example.user.weshare;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.google.common.collect.Lists;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by User on 2015/9/13.
 */
public class UploadVideo extends AsyncTask <Void,Void,JSONArray>{

    private static GoogleAccountCredential credential;
    List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE_UPLOAD);
    private static String VIDEO_FILE_FORMAT = "video/*";
    private static final JsonFactory jsonFactory = new GsonFactory();
    private static final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    static final int REQUEST_AUTHORIZATION = 1;
    static final int REQUEST_ACCOUNT_PICKER = 2;
    private static final String IMAGE_FILE_FORMAT = "video/*";
    private static final String SAMPLE_VIDEO_FILENAME = "MOV_0302.mp4";
    private GetUserCallback getUserCallback;
    private static YouTube youtube;
    private InputStreamContent mediaContent;
    ProgressDialog progressDialog;
    Context context;
    String vid;



int flag=-1;
    public UploadVideo(GetUserCallback getUserCallback,Context c)
    {
        context=c;

        this.getUserCallback=getUserCallback;
    }



    public void setCredential(GoogleAccountCredential credential)
    {
        this.credential=credential;
    }



    public void setMediaContent(File file)
    {

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            mediaContent = new InputStreamContent(
                    IMAGE_FILE_FORMAT, new BufferedInputStream(fileInputStream));
            mediaContent.setLength(file.length());
            flag=0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("DebugLog", e.toString());
        }


    }

    public void doIt(){

        // This OAuth 2.0 access scope allows for full read/write access to the
        // authenticated user's account.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");
        Log.d("DebugLog","XD2");
     

        try {
            youtube = new YouTube.Builder(transport, jsonFactory, credential)
                    .setApplicationName("youtube-cmdline-updatevideo-sample").build();

            Log.d("DebugLog", "Uploading: " + SAMPLE_VIDEO_FILENAME);

            // Add extra information to the video before uploading.
            Video videoObjectDefiningMetadata = new Video();

            // Set the video to be publicly visible. This is the default
            // setting. Other supporting settings are "unlisted" and "private."
            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("public");
            videoObjectDefiningMetadata.setStatus(status);
            // Most of the video's metadata is set on the VideoSnippet object.
            VideoSnippet snippet = new VideoSnippet();

            // This code uses a Calendar instance to create a unique name and
            // description for test purposes so that you can easily upload
            // multiple files. You should remove this code from your project
            // and use your own standard names instead.
            Calendar cal = Calendar.getInstance();
            snippet.setTitle("Test Upload via Java on " + cal.getTime());
            snippet.setDescription(
                    "Video uploaded via YouTube Data API V3 using the Java library " + "on " + cal.getTime());

            // Set the keyword tags that you want to associate with the video.
            List<String> tags = new ArrayList<String>();
            tags.add("test");
            tags.add("example");
            tags.add("java");
            tags.add("YouTube Data API V3");
            tags.add("erase me");
            snippet.setTags(tags);

            // Add the completed snippet object to the video resource.
            videoObjectDefiningMetadata.setSnippet(snippet);
            Log.d("DebugLog", "XD4");


            // Insert the video. The command sends three arguments. The first
            // specifies which information the API request is setting and which
            // information the API response should return. The second argument
            // is the video resource that contains metadata about the new video.
            // The third argument is the actual video content.
            YouTube.Videos.Insert videoInsert = youtube.videos()
                    .insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

            // Set the upload type and add an event listener.
            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

            // Indicate whether direct media upload is enabled. A value of
            // "True" indicates that direct media upload is enabled and that
            // the entire media content will be uploaded in a single request.
            // A value of "False," which is the default, indicates that the
            // request will use the resumable media upload protocol, which
            // supports the ability to resume an upload operation after a
            // network interruption or other transmission failure, saving
            // time and bandwidth in the event of network failures.
            uploader.setDirectUploadEnabled(false);
            Log.d("DebugLog", "XD5");
            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                public void progressChanged(MediaHttpUploader uploader) throws IOException {
                    switch (uploader.getUploadState()) {
                        case INITIATION_STARTED:
                            Log.d("DebugLog", "Initiation Started");
                            break;
                        case INITIATION_COMPLETE:
                            Log.d("DebugLog", "Initiation Completed");
                            break;
                        case MEDIA_IN_PROGRESS:
                            Log.d("DebugLog", "Upload in progress");
                            Log.d("DebugLog", "Upload percentage: " + uploader.getProgress());
                            break;
                        case MEDIA_COMPLETE:
                            Log.d("DebugLog", "Upload Completed!");
                            break;
                        case NOT_STARTED:
                            Log.d("DebugLog", "Upload Not Started!");
                            break;
                    }
                }
            };
            uploader.setProgressListener(progressListener);
            Log.d("DebugLog", "XD5.5");
            // Call the API and upload the video.

            Video returnedVideo = videoInsert.execute();
            vid = ""+returnedVideo.getId();


            ParseQuery<ParseObject> query = ParseQuery.getQuery("VidList");
            // Retrieve the object by id
            query.getInBackground("H3ZnfFkzGt", new GetCallback<ParseObject>() {
                public void done(ParseObject gameScore, ParseException e) {
                    if (e == null) {
                        // Now let's update it with some new data. In this case, only cheatMode and score
                        // will get sent to the Parse Cloud. playerName hasn't changed.
                        gameScore.put("vid", vid);
                        gameScore.saveInBackground();
                    }
                }
            });



            /*ParseObject putusertoparse = new ParseObject("VidList");
            putusertoparse.put("vid", ""+returnedVideo.getId());
            putusertoparse.increment("token",1);
            putusertoparse.saveInBackground();*/
            Log.d("DebugLog","XD6");
            // Print data about the newly inserted video from the API response.
            Log.d("DebugLog", "\n================== Returned Video ==================\n");
            Log.d("DebugLog", "  - Id: " + returnedVideo.getId());
            Log.d("DebugLog", "  - Title: " + returnedVideo.getSnippet().getTitle());
            Log.d("DebugLog", "  - Tags: " + returnedVideo.getSnippet().getTags());
            Log.d("DebugLog", "  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
            Log.d("DebugLog","  - Video Count: " + returnedVideo.getStatistics().getViewCount());

        } catch (GoogleJsonResponseException e) {
            Log.d("DebugLog", "GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            e.printStackTrace();
        }catch (UserRecoverableAuthIOException userRecoverableException) {
            getUserCallback.done(userRecoverableException.getIntent());
        }  catch (IOException e) {
            Log.d("DebugLog", "IOException: " + e.toString());
            e.printStackTrace();
        }
         catch (Throwable t) {
            Log.d("DebugLog", "Throwable: " + t.toString());
            t.printStackTrace();
        }
    }


    private static String getTagFromUser() throws IOException {

        String keyword = "";

        Log.d("DebugLog", "Please enter a tag for your video: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        keyword = bReader.readLine();

        if (keyword.length() < 1) {
            // If the user doesn't enter a tag, use the default value "New Tag."
            keyword = "New Tag";
        }
        return keyword;
    }


    private static String getVideoIdFromUser() throws IOException {

        String videoId = "";

        Log.d("DebugLog", "Please enter a video Id to update: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        videoId = bReader.readLine();

        if (videoId.length() < 1) {
            // Exit if the user doesn't provide a value.
            Log.d("DebugLog", "Video Id can't be empty!");
            System.exit(1);
        }

        return videoId;
    }

    @Override
    protected JSONArray doInBackground(Void[] params) {

    //    progressDialog.show();
        Log.d("DebugLog", "testing:" + flag);
        this.doIt();

        return null;
    }
    @Override
    protected void onPostExecute(JSONArray result){
        super.onPostExecute(result);
        getUserCallback.done();

    }


}

