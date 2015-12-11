package com.ibm.nrdaemon;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Map;

/** This is the worker thread, it creates a runnable based on the current Environment and Application
 * it will be added to a thread pool in the main() and run till its configured end
 * Currently only Applications are handled, servers and plugins will be added in the future*/
public class PollThread implements Runnable{

    /** This is a hack to view debug print outs*/
    protected boolean debug = true;

    /** Today's date and time*/
    LocalDateTime dateNow;

    /** Stores the application TimeRange property, which specifies when the worker thread in run() should end*/
    DateTime dateFinish;

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
        this.dateFinish = DateTime.parse(currentApplication.getValue().getTimeRange(), DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public void run() {
        dateNow = LocalDateTime.now();
        while(dateNow.isBefore(dateFinish.toLocalDateTime())) {
            try {
                /**  Makes the request to New Relic */
                String NRResponseData = request.makeApplicationRESTRequest(currentApplication, currentEnvironment);

                /**  Publishes New Relic response data to the HornetQ on the Wildfly AS */
                application.Publish(NRResponseData);

                if (debug){
                    System.out.println(dateNow);
                    System.out.println(currentApplication.getValue().getName() + NRResponseData);
                }

                /**  Sleep is based on properties configuration */
                Thread.sleep(Integer.parseInt(currentApplication.getValue().getTimePollGranularity()));

                /**  Update current Date/Time*/
                dateNow = LocalDateTime.now();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        System.out.println(currentApplication.getValue().getName() + " has ended..");
    }
}
