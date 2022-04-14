package com.example.popfinder.Model;


public class AddGuideModel {

    private String id;
    private String location;
    private String name;
    private String phone;

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    private Boolean isActive;

    public AddGuideModel() {
    }

    public AddGuideModel(String location, String name, String phone) {
        this.location = location;
        this.name = name;
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}