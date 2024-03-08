package com.nike.artemis.model.cdn;

public class CdnRequestEvent {

    private long time;
    private String userType;
    private String user;
    private String method;
    private String path;
    private String status;

    private Long slsTime;

    public CdnRequestEvent(long time, String userType, String user, String method, String path, String status, Long slsTime) {
        this.time = time;
        this.userType = userType;
        this.user = user;
        this.method = method;
        this.path = path;
        this.status = status;
        this.slsTime = slsTime;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSlsTime() {
        return slsTime;
    }

    public void setSlsTime(Long slsTime) {
        this.slsTime = slsTime;
    }

    @Override
    public String toString() {
        return "CdnRequestEvent{" +
                "time=" + time +
                ", userType='" + userType + '\'' +
                ", user='" + user + '\'' +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", status='" + status + '\'' +
                ", slsTime='" + slsTime + '\'' +
                '}';
    }
}
