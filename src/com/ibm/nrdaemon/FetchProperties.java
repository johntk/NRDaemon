package com.ibm.nrdaemon;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Created by Cloud on 06/11/2015.
 */
public class FetchProperties {
    private String mode;
    private List<Environment> environments = new ArrayList<>();

    public List<Environment> getEnvironments() {
        return environments;
    }

    /**
     * This is a hack to view debug print outs
     */
    protected boolean debug = false;


    /**
     * Loads the .properties file into a Properties object,
     * calls the methods that parse the Properties object into environment: names, keys, date-ranges
     */
    public void buildConfig(String configFileName) throws IOException {
        Properties p = new Properties();
        FileInputStream f = new FileInputStream(configFileName);
        p.load(f);

        parseEnvironmentNames(p);
        readEnvironmentKeys(p);
        readEnvironmentDateRange(p);

        String applications = (String) p.get("applications");

        if (applications != null && !applications.equals("")) {
            readEnvironmentApplicationNames(p);
            setMode("applications");
        }  else {
            setMode("No mode");
        }

        System.out.println("DEBUG: mode = " + mode);
    }


    /**
     * Parses all the environment names in the Properties object,
     * creates a new environment for each and adds them to the
     * environments List
     */
    protected void parseEnvironmentNames(Properties p) throws IOException {

        String allEnvironments = p.getProperty("datacenters");
        StringTokenizer st = new StringTokenizer(allEnvironments, ",");

        while (st.hasMoreTokens()) {
            String environmentName = st.nextToken();
            Environment newEnvironment = new Environment(environmentName);
            environments.add(newEnvironment);
        }
    }

    /**
     * There's a unique API key needed for each environment; this is required on
     * the requests we send to New Relic for that environment.
     * this method pull the API keys form the Properties object and
     * sets them in the corresponding environment object.
     */
    protected void readEnvironmentKeys(Properties p) {
        // loop through each environment
        for (Environment env : environments) {
            String environmentName = env.getName();
            String propertyName = "key." + environmentName;
            String key = p.getProperty(propertyName);
            System.out.println("Key = " + key);
            if (null == key) {
                fatalConfigError("No API key was configured for environment '"
                        + environmentName + "'.");
            }

            if (debug)
                System.out.println("DEBUG: API key for " + environmentName
                        + " = " + key);
            env.setApiKey(key);
        }
    }

    /**
     * Get the date-range to use for each environment from  properties object.
     * New Relic will be queried for application usage rates based on date range.
     */
    protected void readEnvironmentDateRange(Properties p) {
        for (Environment env : environments) {
            String environmentName = env.getName();
            String dateRangePropertyTo = "daterange." + environmentName + ".to";
            String dateRangePropertyFrom = "daterange." + environmentName + ".from";
            String to = getStringProperty(p, dateRangePropertyTo);
            String from = getStringProperty(p, dateRangePropertyFrom);

            if (debug)
            System.out.println("DEBUG: date range='" + from + "' to '" + to + "'");
            env.setDateRange(new DateRange(from, to));
        }
    }

    /**
     * This method handles the Map of Application objects for each Environment
     * providing the application name and ID from the property object.
     */
    protected void readEnvironmentApplicationNames(Properties p) {
        for (Environment env : environments) { // for each environment....
            String allApps = p.getProperty("applications");
            StringTokenizer st = new StringTokenizer(allApps, ",");

            while (st.hasMoreTokens()) {
                String appName = st.nextToken();

                if (debug)
                    System.out.println("DEBUG: appName= " + appName);
                // want to create a new application object for each appName
                String appIdKeyProperty = "appid." + env.getName() + "."
                        + appName;
                String appId = getStringProperty(p, appIdKeyProperty);

                if (debug)
                    System.out.println("DEBUG: appIdKeyProperty='"
                            + appIdKeyProperty + "', appId='" + appId + "'");
                Application app = new Application(env, appName);
                app.setId(appId);
                env.addApplication(app);

                // DEBUG
                // System.out.println("application names is: " + appName);
                // System.out.println("application ID is: " + appId);
            }
        }
    }


    /**
     * Look up a string property from the properties object; if it is not found,
     * die miserably.
     *
     * @param p
     * @param key
     * @return
     */
    protected static String getStringProperty(Properties p, String key) {
        String result = p.getProperty(key);
        if (result != null) {
            return (result);
        }
        fatalConfigError("Ouch: property \"" + key
                + "\" is missing from the properties file, but it is required!");
        return (null);
    }

    /**
     * Report a problem with the configuration data we read which prevents us
     * from proceeding.
     */
    public static void fatalConfigError(String message) {
        System.err.println(message);
        System.exit(1);
    }


    /**
     * sets the "mode", only Applications at the moment
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}

