package com.nike.artemis.model.cdn;

public class CdnRequestEvent {

    private long time;
    private String userType;
    private String user;
    private String method;
    private String path;

    public CdnRequestEvent(long time, String userType, String user, String method, String path) {
        this.time = time;
        this.userType = userType;
        this.user = user;
        this.method = method;
        this.path = path;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "CdnRequestEvent{" +
                "time=" + time +
                ", userType='" + userType + '\'' +
                ", user='" + user + '\'' +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
