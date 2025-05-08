package com.example.fyp_clearcanvas;


import java.io.Serializable;

public class Consultation implements Serializable {
    private String consultationId;
    private String imageUrl;
    private String result;
    private String acneType;
    private String skinType;
    private String date;
    private long timestamp;
    private int acneRating;


    public Consultation() {}

    public Consultation(String consultationId, String imageUrl, String result, String acneType,
                        String skinType, String date, long timestamp, int acneRating) {

        this.consultationId = consultationId;
        this.imageUrl = imageUrl;
        this.result = result;
        this.acneType = acneType;
        this.skinType = skinType;
        this.date = date;
        this.timestamp = timestamp;
        this.acneRating = acneRating;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public void setConsultationIdId(String consultationId) {
        this.consultationId = consultationId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAcneType() {
        return acneType;
    }

    public void setAcneType(String acneType) {
        this.acneType = acneType;
    }

    public String getSkinType() {
        return skinType;
    }

    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getAcneRating() {
        return acneRating;
    }

    public void setAcneRating(int acneRating) {
        this.acneRating = acneRating;
    }
}
