package com.ibm.nrdaemon;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.InputStream;
import java.util.Map;

/** All the Request to NewRelic are made in this class, the responses will be stored in a queue*/
public class MakeRequest {

    /** This method makes the REST API request(s) to New Relic*/
    protected String makeApplicationRESTRequest(Map.Entry<String, Application> entry, Environment env) throws Throwable {
        /** Get the application name from the application object stored in the Map<>*/
        String appID = entry.getValue().getId();

        /** Create the string to send in the request to New relic*/
        String url = env.getURL() + appID + env.getMetricNames();

        /** Create the request using Apache API*/
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        /** Set the Environment API key in the header of the request*/
        request.setHeader("X-API-KEY", env.getApiKey());

        /** Send the request and return the response*/
        HttpResponse response = client.execute(request);
        InputStream input = response.getEntity().getContent();
        return IOUtils.toString(input);
    }
}
