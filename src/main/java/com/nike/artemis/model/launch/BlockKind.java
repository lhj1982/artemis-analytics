package com.nike.artemis.model.launch;

import com.nike.artemis.common.CommonConstants;

public enum BlockKind {
    county(CommonConstants.LAUNCH_BLOCK_KIND_COUNTY),
    ipaddress(CommonConstants.LAUNCH_BLOCK_KIND_IP);
    private String value;

    public String asString() {
        return value;
    }

    BlockKind(String value) {
        this.value = value;
    }

    static public BlockKind from(String value) throws IndexOutOfBoundsException {
        switch (value) {
            case CommonConstants.LAUNCH_BLOCK_KIND_COUNTY:
                return county;
            case CommonConstants.LAUNCH_BLOCK_KIND_IP:
                return ipaddress;
        }
        throw new IndexOutOfBoundsException("Block kind not available");
    }
}
