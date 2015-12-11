package com.ibm.nrdaemon;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the entry point of the application, it is responsible for creating the objects that store the
 * property values, creating the worker threads and ading them to the thread pool
 */
public class Daemon {

    /** Object parses properties files into classes*/
    private FetchProperties fetch;

    public static void main(String[] args) throws InterruptedException, IOException {

        Daemon daemon = new Daemon();
        daemon.setupConfig();
        daemon.start();
    }

    public Daemon() {

    }

    /** Calls the method that builds Environment objects (Environment objects store .properties)*/
    private void setupConfig() throws IOException {

        /** Set the location of the properties file*/
        String propertiesFileName = "datacenter.properties";

        /** Create the properties object*/
        fetch = new FetchProperties();

        /**Parse the properties into classes*/
        fetch.buildConfig(propertiesFileName);
    }

    /** Calls the methods responsible for thread creation*/
    public void start() {

        try {
            /** if the properties are set in application mode start processing application data*/
            if ("applications".equalsIgnoreCase(fetch.getMode())) {
                runModeApplications();
            }
            /** Servers and plugins will be added here*/
            /** Else print the mode that was not recognised*/
            else {
                System.out.println("Mode: " + fetch.getMode() + " not recognized");
                System.exit(0);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /** Threads created and added to thread pool*/
    protected void runModeApplications() throws Throwable {

        /** Thread Pool*/
        ExecutorService executor = Executors.newFixedThreadPool(10);

        /** Loop through Environment List<>*/
        for (Environment env : fetch.getEnvironments()) {
            Map<String, Application> mapOfApps = env.getApplications();
            /** Loop through Application Map*/
            for (Map.Entry<String, Application> app : mapOfApps.entrySet()) {

                /** Worker Thread*/
                PollThread worker = new PollThread(app, env);

                /** Add Worker Thread to Thread Pool*/
                executor.execute(worker);
            }
        }
    }
}
