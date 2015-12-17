package com.ibm.nrdaemon;

import com.ibm.nrdaemon.model.Application;
import com.ibm.nrdaemon.model.Environment;
import com.ibm.nrdaemon.operations.FetchProperties;
import com.ibm.nrdaemon.operations.PollThread;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** This is the entry point of the application, it is responsible for creating the objects that store the
 * property values, creating the worker threads and adding them to the thread pool */
public class Daemon {

    /** FetchProperties Object parses properties files into classes*/
    private FetchProperties fetchApp;

    public static void main(String[] args) throws InterruptedException, IOException {

        Daemon daemon = new Daemon();
        daemon.setupConfig();
        daemon.start();
    }

    public Daemon() {

    }

    /** Calls the method that builds Environment objects (Environment objects store .properties)*/
    private void setupConfig() throws IOException {

        /** get the properties file names*/
        String applicationPropFileName = "datacenter.properties";

        /** Create the properties objects*/
        fetchApp = new FetchProperties();

        /**Parse the properties into classes*/
        fetchApp.buildConfig(applicationPropFileName);
    }

    /** Calls the methods responsible for thread creation*/
    public void start() {

        try {
            /** if the properties are set in application mode start processing application data*/
            if ("applications".equalsIgnoreCase(fetchApp.getMode())) {
                runModeApplications();
            }
            /** Servers and plugins will be added here*/
            /** Else print the mode that was not recognised*/
            else {
                System.out.println("Mode: " + fetchApp.getMode() + " not recognized");
                System.exit(0);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /** Threads created and added to thread pool*/
    protected void runModeApplications() throws Throwable {

        /** Thread Pool*/
        ExecutorService executor = Executors.newFixedThreadPool(5);

        /** Loop through Environment List<>*/
        for (Environment env : fetchApp.getEnvironments()) {
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
