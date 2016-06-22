package com.example.user.weshare;

/**
 * Created by User on 2015/9/21.
 */
public class friend {
    String fid;
    String fname;

    public void setName(String name){
        fname=name;
    }
    public void setId(String id){
        fid=id;
    }
    public String getName(){
        return fname;
    }
    public String getId(){
        return fid;
    }
}
