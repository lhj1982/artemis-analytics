package com.nike.artemis;

import com.nike.artemis.model.*;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import java.util.*;

/**
 * this class is for simulation purpose only
 */
public class SnsRequestGenerator implements SourceFunction<RequestEvent> {
    private  Boolean running = true;

    @Override
    public void run(SourceContext<RequestEvent> ctx) throws Exception {

        Random random = new Random();

        // address
//        String[] streetAddr1 = {"泰华梧桐苑诚竹云路73号楼01佳","泰华梧桐苑诚竹云路73号楼02佳","泰华梧桐苑诚竹云路73号楼03佳"};
//        String[] city = {"衡水市","衡水市","衡水市"};
//        String[] county = {"桃城区","桃城区","桃城区"};
//        String[] state = {"CN-13","CN-13","CN-13"};
//        String[] postalCode = {"123456","123457","123458"};
        String country  ="CN";

        Address address1 = new Address("泰华梧桐苑诚竹云路73号楼01佳", "衡水市", "桃区",  "CN-13", "123456", country);

        Address address2 = new Address("泰华梧桐苑诚竹云路73号楼02佳", "衡水市", "桃城区",  "CN-13", "123456", country);

        Address address3= new Address("泰华梧桐苑诚竹云路73号楼03佳", "衡水市", "城区",  "CN-13", "123456", country);

        ArrayList<Address> addresses = new ArrayList<>(Arrays.asList(address1, address2, address3));

        // user
        String[] upmId = {"3332d057-0b09-4741-9203-670c239de573", "4442d057-0b09-4741-9203-670c239de574", "5552d057-0b09-4741-9203-670c239de575"};

        // device
        String[] trueClientIp = {"191.96.86.150", "191.96.86.151", "191.96.86.152"};

        // experience
        String[] appId ={"com.nike.commerce.omega.droid", "com.nike.onenikecommerce"};
        String[] launchId = {"073d92dc-abcb-3bc7-a899-d11572967904"};
        String[] entryId = {"000aadba-d3be-51e9-890f-9ca04a891000", "111aadba-d3be-51e9-890f-9ca04a891111"};

        String[] entityId = entryId;

        // extras
        String[] ISBOT_WEBFLUX_REQUEST_ID = {"964ffea4-11", "964ffea4-22", "964ffea4-33"};
        Boolean[] incrementsCount = {true, false};
        String merch_group = "CN";
        String nikeAppId = "launchentryvalidator";


//        while(running) {
//            RequestEvent requestEvent = new RequestEvent.Builder()
//                    .addresses(new ArrayList<>(Collections.singletonList(addresses.get(random.nextInt(addresses.size())))))
//                    .user(new User(upmId[random.nextInt(upmId.length)]))
//                    .device(new Device(trueClientIp[random.nextInt(trueClientIp.length)]))
//                    .experience(new Experience(appId[random.nextInt(appId.length)], launchId[random.nextInt(launchId.length)],entryId[random.nextInt(entityId.length)] ))
//                    .entityId(entityId[random.nextInt(entityId.length)])
//                    .extras(new Extras(nikeAppId,ISBOT_WEBFLUX_REQUEST_ID[random.nextInt(ISBOT_WEBFLUX_REQUEST_ID.length)],incrementsCount[random.nextInt(incrementsCount.length)],merch_group))
//                    .timestamp(new Date().getTime())
//                    .build();
//
////            System.out.println(requestEvent);
//
//            ctx.collect(requestEvent);
//            Thread.sleep(1000L);
//        }

    }

    @Override
    public void cancel() {
        running = false;
    }
}
