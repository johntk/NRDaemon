package com.ibm.nrdaemon;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;


/** This class parses the property files into environment classes */
public class FetchProperties {
    private String mode;
    private List<Environment> environments = new ArrayList<>();



    /** This is a hack to view debug print outs*/
    protected boolean debug = false;

    /** Loads the .properties file into a Properties object,
     * calls the methods that parse the Properties object into environment: names, keys*/
    public void buildConfig(String propFileName) throws IOException {
        Properties p = new Properties();
        InputStream f =  getClass().getClassLoader().getResourceAsStream(propFileName);
        p.load(f);

        parseEnvironmentNames(p);
        readEnvironmentValues(p);

        String applications = (String) p.get("applications");

        if (applications != null && !applications.equals("")) {
            readEnvironmentApplicationNames(p);
            setMode("applications");
        }  else {
            setMode("No mode");
        }

        System.out.println("DEBUG: mode = " + mode);
    }


    /** Parses all the environment names in the Properties object,
     * creates a new environment for each and adds them to the
     * environments List*/
    protected void parseEnvironmentNames(Properties p) throws IOException {

        String allEnvironments = p.getProperty("datacenters");
        StringTokenizer st = new StringTokenizer(allEnvironments, ",");

        while (st.hasMoreTokens()) {
            String environmentName = st.nextToken();
            Environment newEnvironment = new Environment(environmentName);
            environments.add(newEnvironment);
        }
    }

    /** There's a unique API key needed for each environment; this is required on
     * the requests we send to New Relic for that environment.
     * this method pull the API keys form the Properties object and
     * sets them in the corresponding environment object.*/
    protected void readEnvironmentValues(Properties p) {
        /** loop through each environment*/
        for (Environment env : environments) {
            String environmentName = env.getName();
            String URL = p.getProperty("url."+ environmentName);
            String metricNames = p.getProperty("metricNames."+ environmentName);
            String key = p.getProperty("key." + environmentName);

            System.out.println("Key = " + key);
            if (null == key) {
                fatalConfigError("No API key was configured for environment '" + environmentName + "'.");
            }

            if (debug)
                System.out.println("DEBUG: API key for " + environmentName + " = " + key);

            env.setApiKey(key);
            env.setMetricNames(metricNames);
            env.setURL(URL);
        }
    }


    /** This method handles the Map of Application objects for each Environment
     * providing the application name and ID from the property object.*/
    protected void readEnvironmentApplicationNames(Properties p) {

        for (Environment env : environments) {
            /** Get all the application names from the properties file */
            String allApps = p.getProperty("applications");
            StringTokenizer st = new StringTokenizer(allApps, ",");
            /** get the environment names */
            String environmentName = env.getName();

            /** split the application names string up into individual names and create application objects*/
            while (st.hasMoreTokens()) {
                String appName = st.nextToken();

                if (debug)
                    System.out.println("DEBUG: appName= " + appName);

                String timeRange = p.getProperty("timeRange."+ environmentName+ "." + appName);
                String timePollGranularity = p.getProperty("timePollGranularity."+ environmentName+ "." + appName);
                String appId = getStringProperty(p, "appid." + env.getName() + "." + appName);

                if (debug)
                    System.out.println("DEBUG: appIdKeyProperty='" + appId + "', appId='" + appId + "'");

                /** Create a new application object for each appName*/
                Application app = new Application(env, appName);
                app.setId(appId);
                app.setTimePollGranularity(timePollGranularity);
                app.setTimeRange(timeRange);
                env.addApplication(app);
            }
        }
    }

    public List<Environment> getEnvironments() {return environments;}

    /** Sets the "mode", only Applications at the moment*/
    public void setMode(String mode) {this.mode = mode;}

    public String getMode() {return mode;}



    /** Look up a string property from the properties object; if it is not found,
     * die miserably.*/
    protected static String getStringProperty(Properties p, String key) {
        String result = p.getProperty(key);
        if (result != null) {
            return (result);
        }
        fatalConfigError("Ouch: property \"" + key
                + "\" is missing from the properties file, but it is required!");
        return (null);
    }

    /** Report a problem with the configuration data we read which prevents us
     * from proceeding.*/
    public static void fatalConfigError(String message) {
        System.err.println(message);
        System.exit(1);
    }
}

