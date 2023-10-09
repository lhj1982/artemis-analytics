package com.nike.artemis.model.waf;

public class WafRequestEvent {
    private long time;
    private WafUserType userType;
    private String user;
    private String method;
    private String path;
    private String status;

    public WafRequestEvent(long time, WafUserType userType, String user, String method, String path,String status) {
        this.time = time;
        this.userType = userType;
        this.user = user;
        this.method = method;
        this.path = path;
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public WafUserType getUserType() {
        return userType;
    }

    public void setUserType(WafUserType userType) {
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

    @Override
    public String toString() {
        return "WafRequestEvent{" +
                "time=" + time +
                ", userType='" + userType + '\'' +
                ", user='" + user + '\'' +
                ", method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

