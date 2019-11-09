package com.transports.expandable_list.tickets_list;

import com.transports.utils.Constants;
import com.transports.utils.UtilityFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Ticket implements Serializable {

    private String details;   //the json format received from ticket service
    private String originDestination;
    private String transports;
    private String schedule;
    private String state;
    private String datePurchased;
    private int id;

    public Ticket(int id, String originDestination, String transports, String schedule, String datePurchased, String status, String details) {
        this.id = id;
        this.originDestination = originDestination;
        this.transports = transports;
        this.schedule = schedule;
        this.datePurchased = datePurchased;
        this.state = status;
        this.details = details;
    }

    public Ticket(String transports, String originDestination, String schedule) {
        this.originDestination = originDestination;
        this.transports = transports;
        this.schedule = schedule;
    }

    public Ticket(int id, String details, String status) {
        this.id = id;
        this.details = details;
        this.state = status;
        parseDetailsAndSetFields();
    }

    public String getOriginDestination() {
        return originDestination;
    }

    public void setOriginDestination(String originDestination) {
        this.originDestination = originDestination;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTransports() {
        return transports;
    }

    public void setTransports(String transports) {
        this.transports = transports;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getState() {
        return state;
    }

    public String getDuration() {
        String startHour = this.schedule.split("-")[0];
        String endHour = this.schedule.split("-")[1];
        return UtilityFunctions.getDifferentBetweenHours(startHour, endHour);
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDatePurchased() {
        return datePurchased;
    }

    public void setDatePurchased(String datePurchased) {
        this.datePurchased = datePurchased;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void parseDetailsAndSetFields(){
        if (details != null && !details.isEmpty()){
            try {
                JSONObject j = new JSONObject(details);
                this.datePurchased = j.getString(Constants.DATE_FIELD);
                j = j.getJSONObject(Constants.TICKET_INFO_FIELD);
                this.transports = j.getString(Constants.COMPANY);
                this.schedule = j.getString(Constants.SCHEDULE);
                this.originDestination = j.getString(Constants.TRIP);
            } catch (JSONException e) {
                e.printStackTrace();
            } {

            }
        }
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "details='" + details + '\'' +
                ", originDestination='" + originDestination + '\'' +
                ", transports='" + transports + '\'' +
                ", schedule='" + schedule + '\'' +
                ", state='" + state + '\'' +
                ", datePurchased='" + datePurchased + '\'' +
                ", id=" + id +
                '}';
    }
}
