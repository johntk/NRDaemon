package com.ibm.nrdaemon;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Created by Cloud on 06/11/2015.
 */
public class FetchProperties {

    private List<Environment> environments = new ArrayList<>();

    public List<Environment> getEnvironments() {
        return environments;
    }

    protected void parseEnvironmentNames(Properties p) throws IOException {

        InputStream prop = new FileInputStream("data/applicationTemplates/datacenterALL.properties");
        p.load(prop);
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
     */
    protected void readEnvironmentKeys(Properties p) {
        // loop through each environment
        for (Environment env : environments) {
            String environmentName = env.getName();
            String propertyName = "key." + environmentName;
            String key = p.getProperty(propertyName);
            if (null == key) {
                System.out.println("No API key was configured for environment '"
                        + environmentName + "'.");
            }

            env.setApiKey(key);
        }
    }

    protected void readEnvironmentAppID(Properties p) {
        // loop through each environment
        for (Environment env : environments) {
            String environmentName = env.getName();
            String environmentID = env.getAppID();
            String propertyName = "appid." + environmentName +"." + environmentID;
            String key = p.getProperty(propertyName);
            if (null == key) {
                System.out.println("No API key was configured for environment '"
                        + environmentName + "'.");
            }

            env.setApiKey(key);
        }
    }

    protected void readEnvironmentDateRange(Properties p) {
        for (Environment env : environments) {
            String environmentName = env.getName();
            String dateRangePropertyTo = "daterange." + environmentName + ".to";
            String dateRangePropertyFrom = "daterange." + environmentName + ".from";
//            String to = getStringProperty(p, dateRangePropertyTo);
//            String from = getStringProperty(p, dateRangePropertyFrom);

            // if (debug)

//            System.out.println("DEBUG: date range='" + from + "' to '" + to + "'");
//            env.setDateRange(new DateRange(from, to));
        }
    }
}
