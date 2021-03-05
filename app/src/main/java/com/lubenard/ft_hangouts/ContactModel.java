package com.lubenard.ft_hangouts;

import android.media.Image;

public class ContactModel {
    String name;
    String phoneNumber;
    Image contactImage;

    public ContactModel(String name, String phoneNumber, Image contactImage) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.contactImage = contactImage;
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
