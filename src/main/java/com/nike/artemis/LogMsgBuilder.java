package com.nike.artemis;

import com.nike.artemis.model.LogMsg;

public class LogMsgBuilder {

    private LogMsgBuilder() {
    }

    public static LogMsgBuilder getInstance() {
        return new LogMsgBuilder();
    }

    private String source;
    private String msg;
    private Object data;
    private String exception;

    public LogMsgBuilder source(String source) {
        this.source = source;
        return this;
    }

    public LogMsgBuilder msg(String msg) {
        this.msg = msg;
        return this;
    }

    public LogMsgBuilder data(Object data) {
        this.data = data;
        return this;
    }

    public LogMsgBuilder exception(String exception) {
        this.exception = exception;
        return this;
    }

    public LogMsg build() {
        return new LogMsg(this.source, this.msg, this.data, this.exception);
    }

}
