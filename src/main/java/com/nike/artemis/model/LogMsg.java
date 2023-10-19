package com.nike.artemis.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.nike.artemis.model.block.Block;

public class LogMsg {
    private String source;
    private String msg;
    private Object data;
    private String exception;
    private Block block;
    private String ruleName;
    private String path;


    public LogMsg() {
    }

    public LogMsg(String source, String msg, Object data, String exception, Block block, String ruleName, String path) {
        this.source = source;
        this.msg = msg;
        this.data = data;
        this.exception = exception;
        this.block = block;
        this.ruleName = ruleName;
        this.path = path;
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

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
