package com.example.fyp_clearcanvas;


import java.io.Serializable;

public class Consultation implements Serializable {
    private String id;
    private String imageUrl;
    private String result;
    private String acneType;
    private String skinType;
    private String date;
    private long timestamp;

    public Consultation() {}

    public Consultation(String id, String imageUrl, String result, String acneType, String skinType, String date, long timestamp) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.result = result;
        this.acneType = acneType;
        this.skinType = skinType;
        this.date = date;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getAcneType() { return acneType; }
    public void setAcneType(String acneType) { this.acneType = acneType; }

    public String getSkinType() { return skinType; }
    public void setSkinType(String skinType) { this.skinType = skinType; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
