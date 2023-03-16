package com.nike.artemis;

public enum BlockKind {
    upmid(1),
    county(2),
    trueClientIp(3);
    private int value;

    BlockKind(int value) {
        this.value = value;
    }
}
