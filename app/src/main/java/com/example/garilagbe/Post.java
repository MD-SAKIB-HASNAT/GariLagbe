package com.example.garilagbe;

public class Post {
    private String userId;
    private String title;
    private String description;
    private String price;
    private String location;
    private String type;
    private String imageBase64;

    // Empty constructor needed for Firebase
    public Post() {}

    // Getters (can add setters if needed)
    public String getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getImageBase64() { return imageBase64; }
}
