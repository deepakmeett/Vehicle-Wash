package com.example.bikewash.model;
public class DashboardModel {

    private String vehicle_model;
    private String reach_time;
    private String running_number;
    private String vehicle_type;

    public DashboardModel() {
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public void setVehicle_model(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }

    public String getReach_time() {
        return reach_time;
    }

    public void setReach_time(String reach_time) {
        this.reach_time = reach_time;
    }

    public String getRunning_number() {
        return running_number;
    }

    public void setRunning_number(String running_number) {
        this.running_number = running_number;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }
}