package com.nike.artemis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.Serializable;

public class LogMsg {
    //    private static final long serialVersionUID = -8223396713006724441L;
    private String source;
    private String msg;
    private Object data;
    private Exception exception;


    public LogMsg() {
    }

    public LogMsg(String source, String msg, Object data, Exception exception) {
        this.source = source;
        this.msg = msg;
        this.data = data;
        this.exception = exception;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        try {
            return new JsonMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
