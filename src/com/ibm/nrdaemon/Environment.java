package com.ibm.nrdaemon;

import java.util.HashMap;
import java.util.Map;

/**
 * This class models the environments based on the variables pulled from the .properties,
 * each Environment can have multiple Applications, Servers or Plugins identified by a unique id
 */
public class Environment {


    private final String name;
    private String apiKey;
    private DateRange dateRange;
    private final Map<String, Application> applications;


    public Environment(String envName) {
        this.name = envName;
        this.apiKey = null;
        this.dateRange = new DateRange(null, null);
        applications = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String envKey) {
        this.apiKey = envKey;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public Map<String, Application> getApplications() {
        return applications;
    }

    public void addApplication(Application app) {
        String appName = app.getName();
        applications.put(appName, app);
    }
}
