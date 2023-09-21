package com.nike.artemis;

import org.junit.Assert;
import org.junit.Test;

public class BlockKindTest {

    @Test
    public void testActorKind() throws Exception {
        BlockKind kind = BlockKind.upmid;
        Assert.assertEquals("upmid", kind.toString());
        Assert.assertEquals("ipaddress", BlockKind.ipaddress.toString());
        Assert.assertEquals("county", BlockKind.county.toString());

        Assert.assertEquals(kind.asInt(), 1);
        Assert.assertEquals(BlockKind.county.asInt(), 2);
        Assert.assertEquals(BlockKind.ipaddress.asInt(), 3);

        boolean exceptionThrown = false;
        try{
            BlockKind.fromInt(4);
        }catch (IndexOutOfBoundsException e) {
            exceptionThrown = true;
        }
        Assert.assertTrue(exceptionThrown);
    }
}
