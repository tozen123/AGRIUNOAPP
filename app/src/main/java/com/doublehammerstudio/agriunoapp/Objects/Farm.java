package com.doublehammerstudio.agriunoapp.Objects;

public class Farm {
    private String name;
    private String desc;
    private String areaOfLand;
    private String ownerName;
    private String recording;
    private String municipality;


    public Farm() {
        // Default constructor required for calls to DataSnapshot.getValue(Farm.class)
    }

    public Farm(String name, String desc, String areaOfLand, String ownerName, String recording, String municipality) {
        this.name = name;
        this.desc = desc;
        this.areaOfLand = areaOfLand;
        this.ownerName = ownerName;
        this.recording = recording;
        this.municipality = municipality;
    }
    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.name = municipality;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return desc;
    }

    public void setDescription(String desc) {
        this.desc = desc;
    }

    public String getAreaOfLand() {
        return areaOfLand;
    }

    public void setAreaOfLand(String areaOfLand) {
        this.desc = areaOfLand;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.desc = ownerName;
    }

    public String getRecording() {
        return recording;
    }

    public void setRecording(String recording) {
        this.desc = recording;
    }
}
