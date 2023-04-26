package com.nike.artemis.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Experience{
    public String appId;
    public String launchId;
    public String entryId;

    public Experience(String appId, String launchId, String entryId) {
        this.appId = appId;
        this.launchId = launchId;
        this.entryId = entryId;
    }

    public Experience() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getLaunchId() {
        return launchId;
    }

    public void setLaunchId(String launchId) {
        this.launchId = launchId;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    @Override
    public String toString() {
        return "Experience{" +
                "appId='" + appId + '\'' +
                ", launchId='" + launchId + '\'' +
                ", entryId='" + entryId + '\'' +
                '}';
    }
}
