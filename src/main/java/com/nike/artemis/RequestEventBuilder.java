package com.nike.artemis;

import com.nike.artemis.model.*;

import java.util.ArrayList;

public class RequestEventBuilder {

    public ArrayList<Address> addresses;
    public User user;
    public Device device;
    public Experience experience;
    public String entityId;
    public Extras extras;
    public Long timestamp;

    public RequestEventBuilder() {
    }

    public RequestEventBuilder( RequestEvent requestEvent) {
        this.addresses = requestEvent.addresses;
        this.user = requestEvent.user;
        this.device = requestEvent.device;
        this.experience = requestEvent.experience;
        this.entityId = requestEvent.entityId;
        this.extras = requestEvent.extras;
        this.timestamp = requestEvent.timestamp;
    }
     public RequestEvent build() {
        return new RequestEvent();
     }
}
