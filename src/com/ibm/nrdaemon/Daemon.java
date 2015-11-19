package com.ibm.nrdaemon;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Cloud on 06/11/2015.
 */
public class Daemon implements Runnable {

    private FetchProperties fetch;

    public Daemon() {
    }

    /**
     * This will be threads for each Environment
     * ( possibly parse all applications/servers/plugins into environment objects,
     * then start a new thread for each environment, which in turn makes the request to NewRelic)
     */
    public void run() throws IllegalStateException {
        try {
            /**
             * Set the location of the properties file
             */
//          d.setupConfig("data/applicationTemplates/datacenterALL.properties");
            setupConfig("data/datacenter.properties");
            if ("applications".equalsIgnoreCase(fetch.getMode())) {
                runModeApplications();
            } else {
                System.out.println("Mode: " + fetch.getMode() + " not recognized");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calls the method that builds Environment objects (Environment objects store .properties)
     */
    private void setupConfig(String configFileName) throws IOException {
        fetch = new FetchProperties();
        fetch.buildConfig(configFileName);
    }

    /**
     * Calls the method that makes a request to NewRelic
     */
    protected void runModeApplications() throws IOException {
        MakeRequest request = new MakeRequest();
        request.makeApplicationRESTRequest(fetch);
    }
}
