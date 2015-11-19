package com.ibm.nrdaemon;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.List;

/**
 * The Application class contains the friendly name of the application, the ID value
 * of the application (used by NewRelic), and the value of the max request-per-minute.
 */
public class Application extends EnvironmentChild {

    protected String json;
    List<Integer> dailyAvgList;
    List<Integer> dailyMaxList;
    List<Integer> weekDailyAvgList;
    List<Integer> weekDailyMaxList;
    List<Integer> weeklyAvgList;
    List<Integer> weeklyMaxList;

	public Application(Environment environment, String name) {
        super(environment, name);

        this.json = null;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

//    public Application DEBUG() throws  IOException {
//        throughput.parse(json, getEnvironment().getOptions().getGranularity());
//        return this;
//    }
    
    public void setDailyAvgList (List<Integer> dailyAvgList2) {
    	this.dailyAvgList = dailyAvgList2;
    }
    public List<Integer> getDailyAvgList() {
    	return dailyAvgList;
    }

    public void setDailyMaxList (List<Integer> dailyMaxList) {
    	this.dailyMaxList = dailyMaxList;
    }
    public List<Integer> getDailyMaxList() {
    	return dailyMaxList;
    }
    
    public void setWeekDailyAvgList (List<Integer> weekDailyAvgList) {
    	this.weekDailyAvgList = weekDailyAvgList;
    }
    public List<Integer> getWeekDailyAvgList() {
    	return weekDailyAvgList;
    }
    
    public void setWeekDailyMaxList (List<Integer> weekDailyMaxList) {
    	this.weekDailyMaxList = weekDailyMaxList;
    }
    public List<Integer> getWeekDailyMaxList() {
    	return weekDailyMaxList;
    }
    
    public void setWeeklyAvgList (List<Integer> weeklyAvgList) {
    	this.weeklyAvgList = weeklyAvgList;
    }
    public List<Integer> getWeeklyAvgList() {
    	return weeklyAvgList;
    }
    
    public void setWeeklyMaxList (List<Integer> weeklyMaxList) {
    	this.weeklyMaxList = weeklyMaxList;
    }

    public List<Integer> getWeeklyMaxList() {
    	return weeklyMaxList;
    }
    
//    public TimeSeriesCollection getDsWeekly() {
//		return dsWeekly;
//	}

//	public void setDsWeekly(TimeSeriesCollection dsWeekly) {
//		this.dsWeekly = dsWeekly;
//	}
    
    @Override
    public void query(DateRange dateRange) throws ClientProtocolException, IOException {
        System.out.printf("AQUERY: %s.%s%n", environment.getName(), getName());
//        String apiQuery = NewRelicAPIUtils.buildQuery(API_TYPE_APPLICATIONS, API_QUERY_THROUGHPUT, id, dateRange.getFrom(), dateRange.getTo());
        String apiKey = environment.getApiKey();

//        json = NewRelicAPIUtils.executeJsonRequest(apiQuery, apiKey);

//        parse();
    }
}
