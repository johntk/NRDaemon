package com.ibm.nrdaemon;

/** The Application class contains the friendly name of the application, the ID value
 * of the application (used by NewRelic), and the value of the max request-per-minute. */
public class Application  {

    private Environment environment;
    private String name;
    private String id;
    private String timeRange;
    private String timePollGranularity;

	public Application(Environment environment, String name) {

        this.environment = environment;
        this.name = name;
        this.id = null;
        this.timeRange = null;
        this.timePollGranularity = null;
    }

    public Environment getEnvironment() {return environment;}

    public String getName() {
        return name;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getTimeRange() {return timeRange;}

    public void setTimeRange(String timeRange) {this.timeRange = timeRange;}

    public String getTimePollGranularity() {return timePollGranularity;}

    public void setTimePollGranularity(String timePollGranularity) {this.timePollGranularity = timePollGranularity;}

}
