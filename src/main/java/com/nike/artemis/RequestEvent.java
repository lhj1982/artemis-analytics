package com.nike.artemis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nike.artemis.model.*;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestEvent {

    public ArrayList<Address> addresses;
    public User user;
    public Device device;
    public Experience experience;
    public String entityId;
    public Extras extras;
    public Long timestamp;

    public RequestEvent() {
    }

    public static class Builder {
        private ArrayList<Address> addresses;
        private User user;
        private Device device;
        private Experience experience;
        private String entityId;
        private Extras extras;
        private Long timestamp;

        public Builder addresses(ArrayList<Address> addresses) {
            this.addresses = addresses;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder device(Device device) {
            this.device = device;
            return this;
        }

        public Builder experience(Experience experience) {
            this.experience = experience;
            return this;
        }

        public Builder entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder extras(Extras extras) {
            this.extras = extras;
            return this;
        }

        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public RequestEvent build() {
            RequestEvent requestEvent = new RequestEvent();
            requestEvent.addresses = this.addresses;
            requestEvent.user = this.user;
            requestEvent.device = this.device;
            requestEvent.experience = this.experience;
            requestEvent.entityId = this.entityId;
            requestEvent.extras = this.extras;
            requestEvent.timestamp = this.timestamp;
            return requestEvent;
        }
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Extras getExtras() {
        return extras;
    }

    public void setExtras(Extras extras) {
        this.extras = extras;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestEvent that = (RequestEvent) o;

        if (addresses != null ? !addresses.equals(that.addresses) : that.addresses != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (device != null ? !device.equals(that.device) : that.device != null) return false;
        if (experience != null ? !experience.equals(that.experience) : that.experience != null) return false;
        if (entityId != null ? !entityId.equals(that.entityId) : that.entityId != null) return false;
        if (extras != null ? !extras.equals(that.extras) : that.extras != null) return false;
        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = addresses != null ? addresses.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (device != null ? device.hashCode() : 0);
        result = 31 * result + (experience != null ? experience.hashCode() : 0);
        result = 31 * result + (entityId != null ? entityId.hashCode() : 0);
        result = 31 * result + (extras != null ? extras.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "com.nike.artemis.RequestEvent{" +
                "addresses=" + addresses +
                ", user=" + user +
                ", device=" + device +
                ", experience=" + experience +
                ", entityId='" + entityId + '\'' +
                ", extras=" + extras +
                ", timestamp=" + timestamp +
                '}';
    }
}
