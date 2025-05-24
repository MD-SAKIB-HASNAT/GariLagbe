package com.example.garilagbe;

import java.io.Serializable;

public class Post  implements Serializable {
    private String userId;
    private String title;
    private String description;
    private String price;
    private String location;
    private String type;
    private String fuelType;
    private String mileage;
    private String contact;
    private String imageBase64;
    private String time;
    private String date;
    private String postId;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    // Empty constructor needed for Firebase
    public Post() {}

    // Getters
    public String getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getFuelType() { return fuelType; }
    public String getMileage() { return mileage; }
    public String getContact() { return contact; }
    public String getImageBase64() { return imageBase64; }
    public String getTime() { return time; }
    public String getDate() { return date; }

    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(String price) { this.price = price; }
    public void setLocation(String location) { this.location = location; }
    public void setType(String type) { this.type = type; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public void setMileage(String mileage) { this.mileage = mileage; }
    public void setContact(String contact) { this.contact = contact; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
    public void setTime(String time) { this.time = time; }
    public void setDate(String date) { this.date = date; }
}
