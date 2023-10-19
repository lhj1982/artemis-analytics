package com.nike.artemis;

import com.nike.artemis.model.LogMsg;
import com.nike.artemis.model.block.Block;

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
    private Block block;
    private String ruleName;
    private String path;

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

    public LogMsgBuilder block(Block block) {
        this.block = block;
        return this;
    }

    public LogMsgBuilder ruleName(String ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    public LogMsgBuilder path(String path) {
        this.path = path;
        return this;
    }

    public LogMsg build() {
        return new LogMsg(this.source, this.msg, this.data, this.exception, this.block, this.ruleName, this.path);
    }

}
