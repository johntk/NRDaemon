package com.ibm.nrdaemon;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by Cloud on 06/11/2015.
 */
public class MakeRequest {

    private static final String appID = "7513522";
    private static final String nrUrl = "https://api.newrelic.com/v2/applications/";
    private static final String names = "/metrics/data.json?names[]=HttpDispatcher&values[]=requests_per_minute&from=2015-11-05T22:10:00+00:00";
    private Properties p = new Properties();
    private FetchProperties fetch;


    /**
     * This method makes the REST API request(s) to New Relic to retrieve the
     * data
     */
    protected void makeApplicationRESTRequest() throws IllegalStateException, IOException {
        CloseableHttpClient client;
        InputStream input;
        fetch = new FetchProperties();
        fetch.parseEnvironmentNames(p);
        fetch.readEnvironmentKeys(p);


        for(Environment env : fetch.getEnvironments() ){
            String url = nrUrl + appID + names;
            client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            request.setHeader("X-API-KEY", env.getApiKey());
            HttpResponse response = client.execute(request);

            input = response.getEntity().getContent();
            String json = IOUtils.toString(input);

            System.out.println(json.toString());
        }



        System.out.println("done.");

    }
}
