package com.ibm.nrdaemon.operations;

import com.ibm.nrdaemon.model.Application;
import com.ibm.nrdaemon.model.DateRange;
import com.ibm.nrdaemon.model.Environment;
import org.joda.time.DateTime;
import org.joda.time.Instant;


import java.util.Map;

/** This is the worker thread, it creates a runnable based on the current Environment and Application
 * it will be added to a thread pool in the main() and run till its configured end
 * Currently only Applications are handled, servers and plugins will be added in the future*/
public class PollThread implements Runnable{

    /** This is a hack to view debug print outs*/
    protected boolean debug = true;

    /** Today's date and time*/
    Instant dateNow;

    /** This is the time for the thread to sleep between requests, this will not be hardcoded in the future */
    String dateDelta = "300000";

    Instant dateFrom;
    Instant dateTo;

    /** Request object to make request to New Relic*/
    MakeRequest request = new MakeRequest();

    /** Current environment to request data from New Relic on*/
    Environment currentEnvironment;

    /** Current application to request data from New relic on*/
    Map.Entry<String, Application> currentApplication;

    /** Publisher object which is responsible for sending data to the HornetQ on the Wildfly AS */
    Publisher application = new Publisher();

    public PollThread(Map.Entry<String, Application> app, Environment env){
        this.currentEnvironment = env;
        this.currentApplication = app;

        /** Set the dateFrom  and dateTo based on the properties value, format it using TimestampUtils and Joda-time*/
        this.dateFrom = currentEnvironment.getDateRange().getFrom();
        this.dateTo = currentEnvironment.getDateRange().getTo();
    }

    @Override
    public void run() {
        dateNow = Instant.now();

        /** Check if Todays date is Between thr from and to date range set in the properties file*/
        while(dateNow.isAfter(dateFrom) && dateNow.isBefore(dateTo)) {
            try {

                /**  Wait for first Delta time in order to allow the app to generate the first delta minutes */
                Thread.sleep(Integer.parseInt(dateDelta));

                /**  Makes the request to New Relic */
                String NRResponseData = request.makeApplicationRESTRequest(currentApplication, currentEnvironment);

                /**  Publishes New Relic response data to the HornetQ on the Wildfly AS */
//                application.Publish(NRResponseData);

                if (debug){
//                    System.out.println("Date now inside Worker" + dateNow);
                    System.out.println(NRResponseData);
                }

                /**  Update current Date/Time*/
                dateNow = Instant.now();
                DateRange updateDate = new DateRange(dateNow.toString() ,dateTo.toString());
                currentEnvironment.setDateRange(updateDate);
//                System.out.println(currentEnvironment.getDateRange().getFrom());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        System.out.println(currentApplication.getValue().getName() + " has ended..");
    }
}
