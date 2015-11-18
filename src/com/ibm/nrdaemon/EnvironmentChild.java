package com.ibm.nrdaemon;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;

public abstract class EnvironmentChild {
    protected final Environment environment;
    protected String name;
    protected String id;

    public EnvironmentChild(Environment environment, String name) {
        this.environment = environment;
        this.name = name;
        this.id = null;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract void query(DateRange dateRange) throws ClientProtocolException, IOException;
}
