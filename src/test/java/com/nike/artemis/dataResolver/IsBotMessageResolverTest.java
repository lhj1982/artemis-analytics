package com.nike.artemis.dataResolver;

import com.nike.artemis.model.block.Block;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class IsBotMessageResolverTest {

    @Test
    public void parseIsBotMessage() {
        IsBotMessageResolver isBotMessageResolver = new IsBotMessageResolver();
        List<Block> out = new ArrayList<>();
        String data = "{\"entityId\":\"6104122b-5795-5435-bbfc-3e7f94f5271e\",\"entryId\":\"6104122b-5795-5435-bbfc-3e7f94f5271e\",\"launchId\":\"e84fe18c-9262-4f50-8a06-2a631f8dd1ee\",\"upmId\":\"7f152094-fa47-41b3-aa0e-4d5b4dca4709\",\"externalData\":{},\"isBotResult\":{\"isBot\":true,\"score\":1.0}}";
        ListCollector<Block> collector = new ListCollector<>(out);
        isBotMessageResolver.flatMap(data, collector);
        Assert.assertEquals("7f152094-fa47-41b3-aa0e-4d5b4dca4709", out.get(0).getUser());


    }

}