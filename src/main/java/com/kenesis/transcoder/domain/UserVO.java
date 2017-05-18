package com.kenesis.transcoder.domain;

/**
 * Created by Luyin on 2017-05-01.
 */
public class UserVO {
    private String userid;
    private String userpw;
    private String homelocation;

    @Override
    public String toString() {
        return "UserVO [userid=" + userid + ", userpw=" + userpw + ", homelocation=" + homelocation + "]";
    }
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getUserpw() {
        return userpw;
    }
    public void setUserpw(String userpw) {
        this.userpw = userpw;
    }
    public String getHomelocation() {
        return homelocation;
    }
    public void setHomelocation(String homelocation) {
        this.homelocation = homelocation;
    }
}
