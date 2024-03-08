package com.nike.artemis.model.cdn;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CdnData {
    private Long unixtime;
    private String uuid;
    private String user_info;
    private String client_ip;
    private String user_agent;
    private String method;
    private String return_code;
    private String uri;
    private Long sls_receive_time;

    public Long getUnixtime() {
        return unixtime;
    }

    public void setUnixtime(Long unixtime) {
        this.unixtime = unixtime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUser_info() {
        return user_info;
    }

    public void setUser_info(String user_info) {
        this.user_info = user_info;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getSls_receive_time() {
        return sls_receive_time;
    }

    public void setSls_receive_time(Long sls_receive_time) {
        this.sls_receive_time = sls_receive_time;
    }

    @Override
    public String toString() {
        return "CdnData{" +
                "unixtime='" + unixtime + '\'' +
                ", uuid='" + uuid + '\'' +
                ", user_info='" + user_info + '\'' +
                ", client_ip='" + client_ip + '\'' +
                ", user_agent='" + user_agent + '\'' +
                ", method='" + method + '\'' +
                ", return_code='" + return_code + '\'' +
                ", uri='" + uri + '\'' +
                ", sls_receive_time='" + sls_receive_time + '\'' +
                '}';
    }
}
