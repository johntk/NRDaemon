package com.ibm.nrdaemon;

import com.ibm.nrdaemon.model.Application;
import com.ibm.nrdaemon.model.Environment;
import com.ibm.nrdaemon.operations.FetchProperties;
import com.ibm.nrdaemon.operations.PollThread;
import com.ibm.nrdaemon.operations.Publisher;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** This is the entry point of the application, it is responsible for creating the objects that store the
 * property values, creating the worker threads and adding them to the thread pool */
public class Daemon {

    private static Logger logger = Logger.getLogger(Daemon.class.getName());

    /** FetchProperties Object parses properties files into classes*/
    private FetchProperties fetchApp;
    /** Publisher object which is responsible for sending data to the ActiveMQ Queue on the Wildfly AS */
    private Publisher application;

    public static void main(String[] args) throws InterruptedException, IOException {

        Daemon daemon = new Daemon();
        daemon.setupConfig();
        daemon.start();
        BasicConfigurator.configure();
    }

    public Daemon() {

    }

    /** Calls the method that builds Environment objects (Environment objects store .properties)*/
    private void setupConfig() throws IOException {

        /** get the properties file names*/
        String applicationPropFileName = "ApplicationProp/datacenterALL.properties";

        /** Create the properties objects*/
        fetchApp = new FetchProperties();
        application = new Publisher();

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
            logger.fatal("throwable happen Daemon class!", throwable);
            throwable.printStackTrace();
        }
    }

    /** Threads created and added to thread pool*/
    protected void runModeApplications() throws Throwable {

        /** Thread Pool*/
        ExecutorService executor = Executors.newFixedThreadPool(126);

        /** Loop through Environment List<>*/
        for (Environment env : fetchApp.getEnvironments()) {
            Map<String, Application> mapOfApps = env.getApplications();
            /** Loop through Application Map*/
            for (Map.Entry<String, Application> app : mapOfApps.entrySet()) {

                /** Worker Thread*/
                PollThread worker = new PollThread(app, env, application);

                /** Add Worker Thread to Thread Pool*/
                executor.execute(worker);
            }
        }
    }
}
