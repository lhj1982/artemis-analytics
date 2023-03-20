package com.nike.artemis;

import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;

/**
 * not sure whether we need this or not so let put it here currently
 */
public class BlockFilter extends RichFilterFunction<BlockEvent> {

    ValueStateDescriptor<Long> currentEntityMaxBlockDescription;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        currentEntityMaxBlockDescription = new ValueStateDescriptor<Long>("maxBlockByEntity",Long.TYPE);
    }

    /**
     *
     * @param blockEvent The value to be filtered.
     * @return true if the current blockEndTime is larger than previous blockEndTime of the entity
     * @throws Exception
     */
    @Override
    public boolean filter(BlockEvent blockEvent) throws Exception {

        boolean generateRule = false;

        ValueState<Long> state = getRuntimeContext().getState(currentEntityMaxBlockDescription);

        if (state.value() == null) {
            state.update(0L);
        }
        long previousMaxBlock = state.value();
        if (blockEvent.getEndTime() > previousMaxBlock) {
            generateRule = true;
            state.update(blockEvent.getEndTime());
        }
        return generateRule;
    }
}
