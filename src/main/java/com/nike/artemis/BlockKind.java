package com.nike.artemis;

public enum BlockKind {
    upmid(1),
    county(2),
    trueClientIp(3);
    private int value;
    public int asInt() { return value; }

    BlockKind(int value) {
        this.value = value;
    }
}
