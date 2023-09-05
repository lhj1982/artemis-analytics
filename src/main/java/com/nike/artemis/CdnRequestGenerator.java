package com.nike.artemis;

import com.nike.artemis.model.cdn.CdnRequestEvent;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Date;
import java.util.Random;

/**
 * this class is for simulation purpose only
 */
public class CdnRequestGenerator implements SourceFunction<CdnRequestEvent> {
    private  Boolean running = true;

    @Override
    public void run(SourceContext<CdnRequestEvent> ctx) throws Exception {
        Random random = new Random();

        String[] usertype = {"trueClientIp","upmid"};


        // user
        String[] upmId = {"3332d057-0b09-4741-9203-670c239de573", "4442d057-0b09-4741-9203-670c239de574", "5552d057-0b09-4741-9203-670c239de575"};

        // device
        String[] trueClientIp = {"191.96.86.150", "191.96.86.151", "191.96.86.152"};

        while(running) {
            String userType = usertype[random.nextInt(usertype.length)];

            CdnRequestEvent requestEvent = new CdnRequestEvent(
                    new Date().getTime(),
                    userType,
                    "upmid".equals(userType)?upmId[random.nextInt(upmId.length)]:trueClientIp[random.nextInt(trueClientIp.length)],
                    "GET|POST",
                    "/foo/bar"
            );

            ctx.collect(requestEvent);
            Thread.sleep(1000L);
        }

    }

    @Override
    public void cancel() {
        running = false;
    }
}
