package com.nike.artemis.model;

public enum AccountType {
    ATHLETE("nike:athlete"),
    PLUS("nike:plus"),
    SWOOSH("nike:swoosh");
    private final String type;

    AccountType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
