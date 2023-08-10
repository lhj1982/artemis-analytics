package com.nike.artemis.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Extras{
    public String nikeAppId;
    @JsonProperty("ISBOT_WEBFLUX_REQUEST_ID")
    public String iSBOT_WEBFLUX_REQUEST_ID;
    public boolean incrementsCount;
    public String merch_group;

    public Extras(String nikeAppId, String iSBOT_WEBFLUX_REQUEST_ID, boolean incrementsCount, String merch_group) {
        this.nikeAppId = nikeAppId;
        this.iSBOT_WEBFLUX_REQUEST_ID = iSBOT_WEBFLUX_REQUEST_ID;
        this.incrementsCount = incrementsCount;
        this.merch_group = merch_group;
    }

    public Extras() {
    }

    public String getNikeAppId() {
        return nikeAppId;
    }

    public void setNikeAppId(String nikeAppId) {
        this.nikeAppId = nikeAppId;
    }

    public String getiSBOT_WEBFLUX_REQUEST_ID() {
        return iSBOT_WEBFLUX_REQUEST_ID;
    }

    public void setiSBOT_WEBFLUX_REQUEST_ID(String iSBOT_WEBFLUX_REQUEST_ID) {
        this.iSBOT_WEBFLUX_REQUEST_ID = iSBOT_WEBFLUX_REQUEST_ID;
    }

    public boolean isIncrementsCount() {
        return incrementsCount;
    }

    public void setIncrementsCount(boolean incrementsCount) {
        this.incrementsCount = incrementsCount;
    }

    public String getMerch_group() {
        return merch_group;
    }

    public void setMerch_group(String merch_group) {
        this.merch_group = merch_group;
    }


    @Override
    public String toString() {
        return "Extras{" +
                "nikeAppId='" + nikeAppId + '\'' +
                ", iSBOT_WEBFLUX_REQUEST_ID='" + iSBOT_WEBFLUX_REQUEST_ID + '\'' +
                ", incrementsCount=" + incrementsCount +
                ", merch_group='" + merch_group + '\'' +
                '}';
    }
}