package com.ibm.nrdaemon;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.InputStream;
import java.util.Map;


/**
 * All the Request to NewRelic are made in this class, the responses will be stored in a queue
 */
public class MakeRequest {

    /**
     *  NewRelic Address and Metric names,  Should move these to the properties file
     */
    private static final String nrUrl = "https://api.newrelic.com/v2/applications/";
    private static final String names = "/metrics/data.json?names[]=HttpDispatcher&values[]=requests_per_minute&from=";

    /**
     * This method makes the REST API request(s) to New Relic to retrieve the
     * data
     */
    protected void makeApplicationRESTRequest(FetchProperties fetch) throws Exception {
        CloseableHttpClient client;
        InputStream input;

        for(Environment env : fetch.getEnvironments() ){

            DateRange dateRange = env.getDateRange();
            Map<String, Application> mapOfApps = env.getApplications();
            for (Map.Entry<String, Application> entry : mapOfApps.entrySet()) {
                String appID = entry.getValue().getId();

                /**
                 * dateRange is not working properly and I need to figure out how to time stamp the requests
                 */
                String url = nrUrl + appID + names + dateRange.getFrom() + dateRange.getTo();
                client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(url);
                request.setHeader("X-API-KEY", env.getApiKey());
                HttpResponse response = client.execute(request);

                input = response.getEntity().getContent();
                String json = IOUtils.toString(input);

                /**
                 * These responses need to be stored in a Q ready to be sent
                 * to the preprocessor when it's listening.
                 */
                System.out.println(json);

                /**
                 *  Creates a publisher object which is responsible for
                 *  sending data to the HornetQ on the Wildfly AS
                 */
                Publisher AppData = new Publisher();
                AppData.Send(json);
            }
        }
        System.out.println("done.");
    }



}
