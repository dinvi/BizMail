package com.clientREST.bizMail;

/**
 * Created by daniele on 18/06/16.
 */
public class Request {

    private int id;
    private String type;
    private String state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return  "ID = " + id +
                "\nTYPE = '" + type + '\'' +
                "\nSTATE = '" + state;
    }
}
