package com.example.fyp_clearcanvas;


public class PhotoItem {
    private String id;
    private String imageUrl;
    private String note;
    private String timestamp;

    public PhotoItem() {}

    public PhotoItem(String id, String imageUrl, String note, String timestamp) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.note = note;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getImageUrl() { return imageUrl; }
    public String getNote() { return note; }
    public String getTimestamp() { return timestamp; }

    public void setId(String id) { this.id = id; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setNote(String note) { this.note = note; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
