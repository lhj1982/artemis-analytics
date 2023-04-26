package com.nike.artemis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User{
    public String upmId;

    public String getUpmId() {
        return upmId;
    }

    public void setUpmId(String upmId) {
        this.upmId = upmId;
    }

    public User() {
    }

    public User(String upmId) {
        this.upmId = upmId;
    }

    @Override
    public String toString() {
        return "User{" +
                "upmId='" + upmId + '\'' +
                '}';
    }
}
