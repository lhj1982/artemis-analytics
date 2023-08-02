package com.nike.artemis.model.jwt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtPayload {
    private int iat;
    private int exp;
    private String iss;
    private String jti;
    private String aud;
    private String sbt;
    private int trust;
    private int lat;
    private ArrayList<String> scp;
    private String sub;
    private String prn;
    private String prt;
    private String lrscp;

    public int getIat() {
        return iat;
    }

    public void setIat(int iat) {
        this.iat = iat;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public String getSbt() {
        return sbt;
    }

    public void setSbt(String sbt) {
        this.sbt = sbt;
    }

    public int getTrust() {
        return trust;
    }

    public void setTrust(int trust) {
        this.trust = trust;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public ArrayList<String> getScp() {
        return scp;
    }

    public void setScp(ArrayList<String> scp) {
        this.scp = scp;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getPrn() {
        return prn;
    }

    public void setPrn(String prn) {
        this.prn = prn;
    }

    public String getPrt() {
        return prt;
    }

    public void setPrt(String prt) {
        this.prt = prt;
    }

    public String getLrscp() {
        return lrscp;
    }

    public void setLrscp(String lrscp) {
        this.lrscp = lrscp;
    }
}
