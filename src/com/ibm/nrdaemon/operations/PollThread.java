package com.ibm.nrdaemon.operations;

import com.ibm.nrdaemon.model.Application;
import com.ibm.nrdaemon.model.DateRange;
import com.ibm.nrdaemon.model.Environment;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;


import java.util.Map;

/** This is the worker thread, it creates a runnable based on the current Environment and Application
 * it will be added to a thread pool in the main() and run till its configured end
 * Currently only Applications are handled, servers and plugins will be added in the future*/
public class PollThread implements Runnable{

    private static Logger logger = Logger.getLogger(PollThread.class.getName());
    /** This is a hack to view debug print outs*/
    protected boolean debug = false;
    private Publisher application;
    /** Today's date and time, I may add this to the dateRange class, need to ask Mentor */
    Instant dateNow;

    /** This is the time for the thread to sleep between requests, this will not be hardcoded in the future */
    int dateDelta = 240000;

    /** This is the dateRange for the thread to run*/
    Instant dateFrom;
    Instant dateTo;

    /** Request object to make request to New Relic*/
    MakeRequest request = new MakeRequest();

    /** Current environment to request data from New Relic on*/
    Environment currentEnvironment;

    /** Current application to request data from New relic on*/
    Map.Entry<String, Application> currentApplication;

    /** Publisher object which is responsible for sending data to the HornetQ on the Wildfly AS */
//    Publisher application = new Publisher();

    public PollThread(Map.Entry<String, Application> app, Environment env, Publisher application){
        this.currentEnvironment = env;
        this.currentApplication = app;
        this.application = application;
        /** Set the dateFrom  and dateTo based on the properties value, format it using TimestampUtils and Joda-time*/
        this.dateFrom = currentEnvironment.getDateRange().getFrom();
        this.dateTo = currentEnvironment.getDateRange().getTo();
    }
// ToDO: Add error handling for bad requests
    @Override
    public void run() {
        dateNow = new Instant(LocalDateTime.now().toString());

        /** Check if the date and time now is Between the from and to date range set in the properties file*/
        while(dateNow.isAfter(dateFrom) && dateNow.isBefore(dateTo)) {
            try {

                /** Wait for first Delta time in order to allow the app to generate the first delta minutes*/
                Thread.sleep(dateDelta);

                /** Update dateNow to the current time*/
                dateNow = new Instant(LocalDateTime.now().toString());

                /** Update the currentEnvironment DateRange with the new dateFrom and dateNow values
                 * This will set the New relic request up to pull data based on the dateDelta we have set*/
                DateRange updateDate = new DateRange(dateFrom.toString() ,dateNow.toString());
                currentEnvironment.setDateRange(updateDate);

                /** Makes the request to New Relic*/
                String NRResponseData = request.makeApplicationRESTRequest(currentApplication, currentEnvironment);

                /** Publishes New Relic response data to the HornetQ on the Wildfly AS*/
                application.Publish(NRResponseData);

                if (debug){
                    System.out.println(NRResponseData);

                }

                /**  Update dateFrom with the previous dateTo, this is to insure the
                 * next request starts at the point the last request ended*/
                dateFrom = currentEnvironment.getDateRange().getTo();

                /** Update dateNow to the current time, this is simply refreshing dateNow to insure it is always current*/
                dateNow = new Instant(LocalDateTime.now().toString());

            } catch (Throwable throwable) {
                logger.fatal("throwable happen PollThread class!",throwable);
            }
        }
        System.out.println(currentApplication.getValue().getName() + " has ended..");
    }
}
