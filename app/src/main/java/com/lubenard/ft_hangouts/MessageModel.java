package com.lubenard.ft_hangouts;

public class MessageModel {
    private long id;
    private int contactId;
    private String content;
    private String direction;

    public MessageModel(long id, int contactId, String content, String direction) {
        this.id = id;
        this.contactId = contactId;
        this.content = content;
        this.direction = direction;
    }

    public long getId() {
        return id;
    }

    public int getContactId() {
        return contactId;
    }

    public String getContent() {
        return content;
    }

    public int getDirection(){
        return (direction.equals("FROM")) ? 0: 1;
    }
}
