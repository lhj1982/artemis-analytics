package com.nike.artemis.model.waf;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WafData {
    private String host;
    private String http_user_agent;
    private String https;
    private String http_cookie;
    private String real_client_ip;
    private String status;
    private String request_method;
    private String request_body;
    private String request_path;
    private String request_traceid;
    private Long time;
    private String user_id;
    private String wxbb_info_tbl;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHttp_user_agent() {
        return http_user_agent;
    }

    public void setHttp_user_agent(String http_user_agent) {
        this.http_user_agent = http_user_agent;
    }

    public String getHttps() {
        return https;
    }

    public void setHttps(String https) {
        this.https = https;
    }

    public String getHttp_cookie() {
        return http_cookie;
    }

    public void setHttp_cookie(String http_cookie) {
        this.http_cookie = http_cookie;
    }

    public String getReal_client_ip() {
        return real_client_ip;
    }

    public void setReal_client_ip(String real_client_ip) {
        this.real_client_ip = real_client_ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequest_method() {
        return request_method;
    }

    public void setRequest_method(String request_method) {
        this.request_method = request_method;
    }

    public String getRequest_body() {
        return request_body;
    }

    public void setRequest_body(String request_body) {
        this.request_body = request_body;
    }

    public String getRequest_path() {
        return request_path;
    }

    public void setRequest_path(String request_path) {
        this.request_path = request_path;
    }

    public String getRequest_traceid() {
        return request_traceid;
    }

    public void setRequest_traceid(String request_traceid) {
        this.request_traceid = request_traceid;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWxbb_info_tbl() {
        return wxbb_info_tbl;
    }

    public void setWxbb_info_tbl(String wxbb_info_tbl) {
        this.wxbb_info_tbl = wxbb_info_tbl;
    }

    @Override
    public String toString() {
        return "WafData{" +
                "host='" + host + '\'' +
                ", http_user_agent='" + http_user_agent + '\'' +
                ", https='" + https + '\'' +
                ", http_cookie='" + http_cookie + '\'' +
                ", real_client_ip='" + real_client_ip + '\'' +
                ", status='" + status + '\'' +
                ", request_method='" + request_method + '\'' +
                ", request_body='" + request_body + '\'' +
                ", request_path='" + request_path + '\'' +
                ", request_traceid='" + request_traceid + '\'' +
                ", time='" + time + '\'' +
                ", user_id='" + user_id + '\'' +
                ", wxbb_info_tbl='" + wxbb_info_tbl + '\'' +
                '}';
    }
}
