package com.example.fyp_clearcanvas;


public class WishlistProduct {
    private String productId;
    private String name;
    private double price;
    private String link;

    public WishlistProduct() {

    }

    public WishlistProduct(String productId, String name, double price, String link) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.link = link;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getLink() { return link; }

    public void setProductId(String productId) { this.productId = productId; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setLink(String link) { this.link = link; }
}
