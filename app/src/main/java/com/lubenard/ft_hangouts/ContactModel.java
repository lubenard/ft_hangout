package com.lubenard.ft_hangouts;

import android.media.Image;

public class ContactModel {
    private int id;
    private String name;
    private String phoneNumber;
    private Image contactImage;

    public ContactModel(int id, String name, String phoneNumber, Image contactImage) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.contactImage = contactImage;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Image getContactImage() {
        return contactImage;
    }
}
