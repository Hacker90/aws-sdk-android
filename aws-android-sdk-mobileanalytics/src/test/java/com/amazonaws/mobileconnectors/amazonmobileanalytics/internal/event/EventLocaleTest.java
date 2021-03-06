/*
 * Copyright 2010-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amazonaws.mobileconnectors.amazonmobileanalytics.internal.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.AnalyticsEvent;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.internal.core.AnalyticsContext;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.internal.core.configuration.Configuration;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.internal.core.system.DeviceDetails;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.utils.AnalyticsContextBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventLocaleTest {

    private static final String SDK_NAME = "AppIntelligenceSDK-Analytics";
    private static final String SDK_VERSION = "test";
    private static final String UNIQUE_ID = "abc123";

    @Test
    public void createEvent_verifyLocaleFormat() throws JSONException {
        Map<String, Locale> locales = new HashMap<String, Locale>();
        locales.put("en_US", Locale.US);
        locales.put("en_CA", Locale.CANADA);
        locales.put("fr_CA", Locale.CANADA_FRENCH);
        locales.put("zh_CN", Locale.CHINA);
        locales.put("fr_FR", Locale.FRANCE);
        locales.put("de_DE", Locale.GERMANY);
        locales.put("it_IT", Locale.ITALY);
        locales.put("ja_JP", Locale.JAPAN);
        locales.put("ko_KR", Locale.KOREA);
        locales.put("zh_TW", Locale.TAIWAN);
        locales.put("en_GB", Locale.UK);
        locales.put("ar_SA", new Locale("ar", "SA"));
        locales.put("nl_NL", new Locale("nl", "NL"));
        locales.put("en_AU", new Locale("en", "AU"));
        locales.put("es_ES", new Locale("es", "ES"));
        locales.put("pt_BR", new Locale("pt", "BR"));
        locales.put("es_MX", new Locale("es", "MX"));

        for (String expectedLocaleName : locales.keySet()) {
            Locale expectedLocale = locales.get(expectedLocaleName);

            // create an event client and attach an observer to it
            DefaultEventClient target = new DefaultEventClient(createMockContext(expectedLocale),
                    true);
            EventObserver observer = Mockito.mock(EventObserver.class);
            target.addEventObserver(observer);

            // create the event
            AnalyticsEvent event = target.createEvent("localeEvent");
            target.recordEvent(event);

            ArgumentCaptor<InternalEvent> argument = ArgumentCaptor.forClass(InternalEvent.class);
            verify(observer).notify(argument.capture());

            InternalEvent recordedEvent = argument.getValue();
            JSONObject jsonEvent = recordedEvent.toJSONObject();
            assertThat(jsonEvent.getString("locale"), is(expectedLocaleName));
        }
    }

    private static AnalyticsContext createMockContext(Locale localeToReturn) {
        Configuration mockConfiguration = Mockito.mock(Configuration.class);
        when(mockConfiguration.optString("versionKey", "ver")).thenReturn("ver");
        when(mockConfiguration.optBoolean("isAnalyticsEnabled", true)).thenReturn(true);

        DeviceDetails mockDeviceDetails = Mockito.mock(DeviceDetails.class);
        when(mockDeviceDetails.locale()).thenReturn(localeToReturn);

        AnalyticsContext mockContext = new AnalyticsContextBuilder()
                .withSdkInfo(SDK_NAME, SDK_VERSION)
                .withUniqueIdValue(UNIQUE_ID)
                .withConfiguration(mockConfiguration)
                .withDeviceDetails(mockDeviceDetails)
                .build();
        return mockContext;
    }

}
