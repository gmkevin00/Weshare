package com.example.user.weshare;

import com.parse.ParseFile;

/**
 * Created by User on 2015/9/21.
 */
public class videolist {
    public String uid;
    public String uname;
    public String vid;
    public Double lat;
    public Double lng;
    public String message;
    public Number priority;
    public ParseFile file;
    public void setId(String id){
        uid=id;
    }
    public void setName(String name){
        uname=name;
    }
    public void setVid(String vdoid){
        vid=vdoid;
    }
    public void setLat(Double lat){
        this.lat=lat;
    }
    public void setLng(Double lng){
        this.lng=lng;
    }
    public void setMessage(String message){
        this.message=message;
    }
    public void setPriority(Number priority){
        this.priority=priority;
    }
    public void setFile(ParseFile file){
        this.file=file;
    }
    public String getId(){
        return uid;
    }
    public String getName(){
        return uname;
    }
    public String getVid(){
        return vid;
    }
    public Double getLat(){
        return lat;
    }
    public Double getLng(){
        return lng;
    }
    public String getMessage(){
        return message;
    }
    public Number getPriority(){
        return priority;
    }
    public ParseFile getFile(){
        return file;
    }
}
