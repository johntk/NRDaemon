package com.ibm.nrdaemon.operations;

import com.ibm.nrdaemon.model.Application;
import com.ibm.nrdaemon.model.Environment;
import com.ibm.nrdaemon.model.TimestampUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Map;


/** All the Request to NewRelic are made in this class */
public class MakeRequest {

    /** This method makes the REST API request(s) to New Relic*/
    protected String makeApplicationRESTRequest(Map.Entry<String, Application> entry, Environment env) throws Throwable {
        /** Get the application name from the application object stored in the Map<>*/
        String appID = entry.getValue().getId();
        String appName = entry.getValue().getName();
        String envName = env.getName();

        /** Create the string to send in the request to New relic, works with out using "TimestampUtils.format" may remove after further testing*/
        String url = env.getURL() + appID + env.getMetricNames() + env.getDateRange().getFrom() + "&to=" + TimestampUtils.format(env.getDateRange().getTo()) + "&period=60";

//        System.out.println("Env Name: " + env.getName() + "App Name: " + entry.getValue().getName());
//        System.out.println("TImestamp Parser" +TimestampUtils.format(env.getDateRange().getTo()));

        /** Create the request using Apache API*/
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        /** Set the Environment API key in the header of the request*/
        request.setHeader("X-API-KEY", env.getApiKey());

        /** Send the request and return the response*/
        HttpResponse response = client.execute(request);
        InputStream input = response.getEntity().getContent();

        /** Parse the NR response into a JSONObject*/
        JSONObject JSON = new JSONObject(IOUtils.toString(input));

        /** New JSONobject to send to PreProcessor*/
//        JSONObject applicationId = new JSONObject();
//        JSONObject applicationName = new JSONObject();
        JSONObject environment = new JSONObject();

       /** JSONArray to put application ID and the NR response JSONObject, probably don't need, may remove*/
        JSONArray array = new JSONArray();
        array.put(appName);
        array.put(appID);
        array.put(JSON);

        environment.put(envName, array);

//        /** Put the application name and JSONArray into the new JSONObject*/
//        applicationId.put(envName, array);
//        applicationName.put(appName, applicationId);
//        environment.put(envName, applicationName);
//
//        System.out.println(environment.toString());

        return environment.toString();
    }
}
