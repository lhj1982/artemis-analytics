package com.nike.artemis;

import com.nike.artemis.BlockKind;
import com.nike.artemis.RateRule;
import org.apache.flink.streaming.api.functions.source.SourceFunction;


import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class RuleSource implements SourceFunction<RuleChange> {
    public Boolean running = true;

    @Override
    public void run(SourceContext<RuleChange> ctx) throws Exception {

        running = true;
        // rule: county,桃城区,,,10L,1L,2023-03-12 22:44:00.000,10L,ON
        RuleChange ruleChange = new RuleChange(RuleChange.Action.CREATE, new RateRule(BlockKind.county, "桃城区", "", "", 5L, 1L, LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli(), 10L, RateRule.RuleState.ON));
        System.out.println(ruleChange.rule);
        ctx.collect(ruleChange);


    }

    @Override
    public void cancel() {
        running = false;
    }
}
