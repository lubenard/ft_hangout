package com.lubenard.ft_hangouts;

public class ContactModel {
    private int id;
    private String name;
    private String phoneNumber;
    private String email;
    private String contactImage;

    public ContactModel(int id, String name, String phoneNumber, String email, String contactImage) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.contactImage = contactImage;
        this.email = email;
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

    public String getEmail(){
        return email;
    }

    public String getContactImage() {
        return contactImage;
    }
}
