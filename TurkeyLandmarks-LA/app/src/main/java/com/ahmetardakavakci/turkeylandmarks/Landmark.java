package com.ahmetardakavakci.turkeylandmarks;


import java.io.Serializable;

public class Landmark implements Serializable {

    String name;
    String desc;
    int image;

    public Landmark(String name, String desc, int image) {
        this.name = name;
        this.desc = desc;
        this.image = image;
    }

}
