package com.example.fyp_clearcanvas;


public class Product {
    private String name;
    private double price;
    private String link;

    public Product(String name, double price, String link) {
        this.name = name;
        this.price = price;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getLink() {
        return link;
    }

}
