package com.example.fyp_clearcanvas;

public class Product {
    private String productId;
    private String name;
    private String link;

    public Product() {

    }

    public Product(String productId, String name, String link) {
        this.productId = productId;
        this.name = name;
        this.link = link;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
