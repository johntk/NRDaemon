package com.ibm.nrdaemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class models the environments based on the variables pulled from the .properties
 */
public class Environment {


    private final String name;
    private String apiKey;
    private String appID;
    private DateRange dateRange;


    public Environment(String envName) {
        this.name = envName;
        this.apiKey = null;
        this.dateRange = new DateRange(null, null);
    }

    public String getName() {
        return name;
    }

    public String getApiKey() {
        return apiKey;
    }
    public String getAppID() {return appID;}

    public void setApiKey(String envKey) {
        this.apiKey = envKey;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }


}
