package com.ibm.nrdaemon.operations;

import com.ibm.nrdaemon.model.Application;
import com.ibm.nrdaemon.model.Environment;

import com.ibm.nrdaemon.model.TimestampUtils;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


public class DaemonTest {

    private String applicationPropFileName = "ApplicationProp/datacenterALL.properties";
    private FetchProperties fetchApp;
    private MakeRequest testRequest;

    /** Set up are test variables*/
    @Before
    public void setUp() throws Exception {

        fetchApp = new FetchProperties();
        fetchApp.buildConfig(applicationPropFileName);
        fetchApp.getEnvironments();
        testRequest = new MakeRequest();
    }

    /** Test the correct number of Environment are returned*/
    @Test
    public void FetchPropertiesTest() throws IOException {

        int count =0;
        for (Environment env : fetchApp.getEnvironments()) {

            count++;
        }
        assertEquals(7,count);
    }

    /** Test the request to NR*/
    @Test
    public void MakeRESTRequestTest() throws Throwable {

        /** Loop through Environment List<>*/
        for (Environment env : fetchApp.getEnvironments()) {
            Map<String, Application> mapOfApps = env.getApplications();
            /** Loop through Application Map*/
            for (Map.Entry<String, Application> app : mapOfApps.entrySet()) {

                /** Make the Request*/

                String NRResponseData = testRequest.makeApplicationRESTRequest(app, env);
                System.out.println(NRResponseData);
                assertThat(NRResponseData, not(equalTo("")));
                break;
            }
            break;
        }
    }

    /** Test the TimestampUtils formatting*/
    @Test
    public void TimeStampUtilsTest(){

        String TimeStampFormat = "2015-12-17T16:22:40.000Z";
        Instant TimeStampFormatTest = TimestampUtils.parseTimestamp("2015-12-17T16:22:40+00:00");
        assertEquals(TimeStampFormat, TimeStampFormatTest.toString());
    }
}