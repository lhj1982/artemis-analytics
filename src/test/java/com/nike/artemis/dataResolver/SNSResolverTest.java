package com.nike.artemis.dataResolver;

import com.nike.artemis.dataResolver.SNSResolver;
import com.nike.artemis.model.*;
import com.nike.artemis.model.launch.LaunchRequestEvent;
import org.apache.flink.api.common.functions.util.ListCollector;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SNSResolverTest {

    @Test()
    public void emptySnsDataDoesNotFail() throws Exception {
        SNSResolver snsResolver = new SNSResolver();
        List<LaunchRequestEvent> out = new ArrayList<>();
        ListCollector<LaunchRequestEvent> collector = new ListCollector<>(out);
        snsResolver.flatMap("", collector);
        Assert.assertEquals(0, out.size());
    }

    @Test
    public void parseValidSnsData() throws Exception {
        SNSResolver snsResolver = new SNSResolver();
        List<LaunchRequestEvent> out = new ArrayList<>();
        ListCollector<LaunchRequestEvent> collector = new ListCollector<>(out);
        final LaunchRequestEvent expected = new LaunchRequestEvent.Builder()
                .addresses(new ArrayList<>(Arrays.asList(new Address("上海马戏城", "上海市", "闸北区", "CN-51", null, "XA"))))
                .user(new User("12123434-1212-459e-9c7c-4df29d4b8ccc"))
                .device(new Device("136.226.234.199"))
                .experience(new Experience("com.nike.commerce.omega.droid", "deadbeef-5165-3261-8016-fed50bd23d39", "E2133C98-C974-4F46-BF40-1E7BBD28398A"))
                .entityId("E2133C98-C974-4F46-BF40-1E7BBD28398A")
                .extras(new Extras("launchentryvalidator", "6733897b-6", true, "XA"))
                .timestamp(1694063973265L)
                .build();

        String jsonSnsMessage = "{\"addresses\": [{\"streetAddr1\": \"\\u4e0a\\u6d77\\u9a6c\\u620f\\u57ce\", \"city\": \"\\u4e0a\\u6d77\\u5e02\", \"county\": \"\\u95f8\\u5317\\u533a\", \"state\": \"CN-51\", \"country\": \"XA\"}], \"user\": {\"upmId\": \"12123434-1212-459e-9c7c-4df29d4b8ccc\"}, \"device\": {\"trueClientIp\": \"136.226.234.199\"}, \"products\": [{}], \"experience\": {\"appId\": \"com.nike.commerce.omega.droid\", \"launchId\": \"deadbeef-5165-3261-8016-fed50bd23d39\", \"entryId\": \"E2133C98-C974-4F46-BF40-1E7BBD28398A\", \"launchMethod\": \"DRAW\"}, \"entityId\": \"E2133C98-C974-4F46-BF40-1E7BBD28398A\", \"extras\": {\"ISBOT_WEBFLUX_REQUEST_ID\": \"6733897b-6\", \"nikeAppId\": \"launchentryvalidator\", \"merch_group\": \"XA\", \"incrementsCount\": true}, \"timestamp\": 1694063973265}";
        snsResolver.flatMap(jsonSnsMessage, collector);
        Assert.assertEquals(1, out.size());
        Assert.assertEquals(expected.experience.appId, out.get(0).experience.appId);
        Assert.assertEquals(expected.entityId, out.get(0).entityId);
        Assert.assertEquals(expected.addresses.get(0).city, out.get(0).addresses.get(0).city);
        Assert.assertEquals(expected.device.trueClientIp, out.get(0).device.trueClientIp);
    }

    @Test()
    public void dontSendData_whenInvalidSnsData() throws Exception {
        SNSResolver snsResolver = new SNSResolver();
        List<LaunchRequestEvent> out = new ArrayList<>();
        ListCollector<LaunchRequestEvent> collector = new ListCollector<>(out);
        String jsonSnsMessage = "{addresses\": [], \"user\": {\"upmId\": \"12123434-1212-459e-9c7c-4df29d4b8ccc\"}, \"device\": {\"trueClientIp\": \"136.226.234.199\"}, \"products\": [{}], \"experience\": {\"appId\": \"com.nike.commerce.omega.droid\", \"launchId\": \"deadbeef-5165-3261-8016-fed50bd23d39\", \"entryId\": \"E2133C98-C974-4F46-BF40-1E7BBD28398A\", \"launchMethod\": \"DRAW\"}, \"entityId\": \"E2133C98-C974-4F46-BF40-1E7BBD28398A\", \"extras\": {\"ISBOT_WEBFLUX_REQUEST_ID\": \"6733897b-6\", \"nikeAppId\": \"launchentryvalidator\", \"merch_group\": \"XA\", \"incrementsCount\": -345}, \"timestamp\": 1694063973265}";
        snsResolver.flatMap(jsonSnsMessage, collector);
        Assert.assertEquals(0, out.size());
    }

    @Test
    public void dontSendData_whenNoAddress() throws Exception {
        SNSResolver snsResolver = new SNSResolver();
        List<LaunchRequestEvent> out = new ArrayList<>();
        ListCollector<LaunchRequestEvent> collector = new ListCollector<>(out);
        String jsonSnsMessage = "{\"user\": {\"upmId\": \"12123434-1212-459e-9c7c-4df29d4b8ccc\"}, \"device\": {\"trueClientIp\": \"136.226.234.199\"}, \"products\": [{}], \"experience\": {\"appId\": \"com.nike.commerce.omega.droid\", \"launchId\": \"deadbeef-5165-3261-8016-fed50bd23d39\", \"entryId\": \"E2133C98-C974-4F46-BF40-1E7BBD28398A\", \"launchMethod\": \"DRAW\"}, \"entityId\": \"E2133C98-C974-4F46-BF40-1E7BBD28398A\", \"extras\": {\"ISBOT_WEBFLUX_REQUEST_ID\": \"6733897b-6\", \"nikeAppId\": \"launchentryvalidator\", \"merch_group\": \"XA\", \"incrementsCount\": true}, \"timestamp\": 1694063973265}";
        snsResolver.flatMap(jsonSnsMessage, collector);
        Assert.assertEquals(0, out.size());
    }
}
