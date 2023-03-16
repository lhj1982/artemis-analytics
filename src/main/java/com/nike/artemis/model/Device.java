package com.nike.artemis.model;



public class Device{
    public String trueClientIp;

    public String getTrueClientIp() {
        return trueClientIp;
    }

    public void setTrueClientIp(String trueClientIp) {
        this.trueClientIp = trueClientIp;
    }

    public Device(String trueClientIp) {
        this.trueClientIp = trueClientIp;
    }

    public Device() {
    }

    @Override
    public String toString() {
        return "Device{" +
                "trueClientIp='" + trueClientIp + '\'' +
                '}';
    }
}
