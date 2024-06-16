package com.example.demo;

import java.util.List;

public class UpdateLog {
    private Integer entry_id;
    private String type;
    private String timestamp;
    private String server_id;
    private Triple update_details;
    private List<String> server_ids;

    public UpdateLog(){}

    public UpdateLog(Integer entry_id, String type, String timestamp, String server_id, Triple update_details, List<String> server_ids) {
        this.entry_id = entry_id;
        this.type = type;
        this.timestamp = timestamp;
        this.server_id = server_id;
        this.update_details = update_details;
        this.server_ids = server_ids;
    }

    public Integer getEntry_id() {
        return entry_id;
    }
    public void setEntry_id(Integer entry_id) {
        this.entry_id = entry_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public Triple getUpdate_details() {
        return update_details;
    }

    public void setUpdate_details(Triple update_details) {
        this.update_details = update_details;
    }

    public List<String> getServer_ids() {
        return server_ids;
    }

    public void setServer_ids(List<String> server_ids) {
        this.server_ids = server_ids;
    }
}
