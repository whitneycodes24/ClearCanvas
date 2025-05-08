package com.example.fyp_clearcanvas;


public class MenuOptions {
    private int menuPicture;
    private String label;

    public MenuOptions(int menuPicture, String label) {
        this.menuPicture = menuPicture;
        this.label = label;
    }

    public int getMenuPicture() {
        return menuPicture;
    }

    public String getLabel() {
        return label;
    }
}
