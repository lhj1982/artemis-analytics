package com.nike.artemis.model.launch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)
public class IsBotData implements Serializable {
    private String id;
    private String launchId;
    private String skuId;
    private String upmId;
    private Map<String, String> validationSummary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLaunchId() {
        return launchId;
    }

    public void setLaunchId(String launchId) {
        this.launchId = launchId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getUpmId() {
        return upmId;
    }

    public void setUpmId(String upmId) {
        this.upmId = upmId;
    }

    public Map<String, String> getValidationSummary() {
        return validationSummary;
    }

    public void setValidationSummary(Map<String, String> validationSummary) {
        this.validationSummary = validationSummary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IsBotData isBotData = (IsBotData) o;

        if (id != null ? !id.equals(isBotData.id) : isBotData.id != null) return false;
        if (launchId != null ? !launchId.equals(isBotData.launchId) : isBotData.launchId != null) return false;
        if (skuId != null ? !skuId.equals(isBotData.skuId) : isBotData.skuId != null) return false;
        if (upmId != null ? !upmId.equals(isBotData.upmId) : isBotData.upmId != null) return false;
        return validationSummary != null ? validationSummary.equals(isBotData.validationSummary) : isBotData.validationSummary == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (launchId != null ? launchId.hashCode() : 0);
        result = 31 * result + (skuId != null ? skuId.hashCode() : 0);
        result = 31 * result + (upmId != null ? upmId.hashCode() : 0);
        result = 31 * result + (validationSummary != null ? validationSummary.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IsBotData{" +
                "id='" + id + '\'' +
                ", launchId='" + launchId + '\'' +
                ", skuId='" + skuId + '\'' +
                ", upmId='" + upmId + '\'' +
                ", validationSummary=" + validationSummary +
                '}';
    }
}
