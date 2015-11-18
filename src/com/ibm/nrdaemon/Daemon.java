package com.ibm.nrdaemon;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Cloud on 06/11/2015.
 */
public class Daemon {

    private FetchProperties fetch;

    public static void main(String [] args) throws IOException, ParseException {
        Daemon d = new Daemon();

        /**
         * Set the location of the properties file
         */
//        d.setupConfig("data/applicationTemplates/datacenterALL.properties");
        d.setupConfig("data/datacenter.properties");
        d.run();
    }

    /**
     * Calls the method that builds Environment objects (Environment objects store .properties)
     */
    private void setupConfig(String configFileName) throws  IOException {
        fetch = new FetchProperties();
        fetch.buildConfig(configFileName);
    }

    /**
     * This will be threads for each Environment
     * ( possibly parse all applications/servers/plugins into environment objects,
     *  then start a new thread for each environment, which in turn makes the request to NewRelic)
     */
    public void run() throws IllegalStateException, IOException, ParseException {
        if ("applications".equalsIgnoreCase(fetch.getMode())) {
            runModeApplications();
        } else {
            System.out.println("Mode: " + fetch.getMode() + " not recognized");
            System.exit(0);
        }
    }

    /**
     * Calls the method that makes a request to NewRelic
     */
    protected void runModeApplications() throws IllegalStateException, IOException, ParseException {
        MakeRequest request = new MakeRequest();
        request.makeApplicationRESTRequest(fetch);
    }
}
