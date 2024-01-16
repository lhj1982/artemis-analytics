package com.nike.artemis.model.launch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nike.artemis.model.Bot;

import java.io.Serializable;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IsBotData implements Serializable {
    private String entityId;
    private String entryId;
    private String launchId;
    private String upmId;
    private Map<String, String> externalData;
    private Bot isBotResult;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getLaunchId() {
        return launchId;
    }

    public void setLaunchId(String launchId) {
        this.launchId = launchId;
    }

    public String getUpmId() {
        return upmId;
    }

    public void setUpmId(String upmId) {
        this.upmId = upmId;
    }

    public Map<String, String> getExternalData() {
        return externalData;
    }

    public void setExternalData(Map<String, String> externalData) {
        this.externalData = externalData;
    }

    public Bot getIsBotResult() {
        return isBotResult;
    }

    public void setIsBotResult(Bot isBotResult) {
        this.isBotResult = isBotResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IsBotData isBotData = (IsBotData) o;

        if (entityId != null ? !entityId.equals(isBotData.entityId) : isBotData.entityId != null) return false;
        if (entryId != null ? !entryId.equals(isBotData.entryId) : isBotData.entryId != null) return false;
        if (launchId != null ? !launchId.equals(isBotData.launchId) : isBotData.launchId != null) return false;
        if (upmId != null ? !upmId.equals(isBotData.upmId) : isBotData.upmId != null) return false;
        if (externalData != null ? !externalData.equals(isBotData.externalData) : isBotData.externalData != null) return false;
        return isBotResult != null ? isBotResult.equals(isBotData.isBotResult) : isBotData.isBotResult == null;
    }

    @Override
    public int hashCode() {
        int result = entityId != null ? entityId.hashCode() : 0;
        result = 31 * result + (entryId != null ? entryId.hashCode() : 0);
        result = 31 * result + (launchId != null ? launchId.hashCode() : 0);
        result = 31 * result + (upmId != null ? upmId.hashCode() : 0);
        result = 31 * result + (externalData != null ? externalData.hashCode() : 0);
        result = 31 * result + (isBotResult != null ? isBotResult.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IsBotData{" +
                "entityId='" + entityId + '\'' +
                ", entryId='" + entryId + '\'' +
                ", launchId='" + launchId + '\'' +
                ", upmId='" + upmId + '\'' +
                ", externalData=" + externalData +
                ", isBotResult=" + isBotResult +
                '}';
    }
}
