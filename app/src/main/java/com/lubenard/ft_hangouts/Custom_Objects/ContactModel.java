package com.lubenard.ft_hangouts.Custom_Objects;

public class ContactModel {
    private int id;
    private String name;
    private String phoneNumber;
    private String email;
    private String contactImage;
    private int isFavourite;
    private String address;
    private String birthday;

    public ContactModel(int id, String name, String phoneNumber, String email, String contactImage, int isFavourite, String address, String birthday) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.contactImage = contactImage;
        this.email = email;
        this.isFavourite = isFavourite;
        this.address = address;
        this.birthday = birthday;
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

    public boolean getIsFavourite () {
        return (isFavourite == 1);
    }

    public String getAddress () {
        return address;
    }

    public String getBirthday () {
        return birthday;
    }

    public void setIsFavourite(int newIsFavourite) {
        this.isFavourite = newIsFavourite;
    }
}
