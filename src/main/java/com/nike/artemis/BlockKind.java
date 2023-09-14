package com.nike.artemis;

public enum BlockKind {
    upmid(1),
    county(2),
    ipaddress(3);
    private int value;
    public int asInt() { return value; }

    BlockKind(int value) {
        this.value = value;
    }

    static public BlockKind fromInt(int value) throws IndexOutOfBoundsException {
        switch (value) {
            case 1:
                return upmid;
            case 2:
                return county;
            case 3:
                return ipaddress;
        }
        throw new IndexOutOfBoundsException("Block kind not available");
    }
}
