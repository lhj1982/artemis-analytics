package com.nike.artemis;

import com.nike.artemis.common.CommonConstants;
import com.nike.artemis.model.launch.BlockKind;
import org.junit.Assert;
import org.junit.Test;

public class BlockKindTest {

    @Test
    public void testActorKind() {
        BlockKind kind = BlockKind.county;
        Assert.assertEquals(CommonConstants.LAUNCH_BLOCK_KIND_COUNTY, kind.toString());
        Assert.assertEquals(CommonConstants.LAUNCH_BLOCK_KIND_IP, BlockKind.ipaddress.toString());

        Assert.assertEquals(kind.asString(), CommonConstants.LAUNCH_BLOCK_KIND_COUNTY);
        Assert.assertEquals(BlockKind.ipaddress.asString(), CommonConstants.LAUNCH_BLOCK_KIND_IP);

        boolean exceptionThrown = false;
        try {
            BlockKind.from("other");
        } catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        Assert.assertTrue(exceptionThrown);
    }
}
