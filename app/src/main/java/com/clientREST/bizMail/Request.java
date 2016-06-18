package com.clientREST.bizMail;

/**
 * Created by daniele on 18/06/16.
 */
public class Request {

    private int id;
    private String type;
    private String state;

    public Request() {
    }

    public Request(int id, String type, String state) {
        this.id = id;
        this.type = type;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return  "ID = " + id +
                "\nTYPE = '" + type + '\'' +
                "\nSTATE = '" + state;
    }
}
