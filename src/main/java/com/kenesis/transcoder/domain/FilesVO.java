package com.kenesis.transcoder.domain;

/**
 * Created by Luyin on 2017-05-01.
 */
public class FilesVO {
    private int fileid;
    private String userid;
    private String location;
    public int getFileid() {
        return fileid;
    }
    public void setFileid(int fileid) {
        this.fileid = fileid;
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "FilesVO [fileid=" + fileid + ", userid=" + userid + ", location=" + location + "]";
    }
}
