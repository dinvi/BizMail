package com.clientREST.bizMail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daniele on 06/06/16.
 */
public class Mail implements Parcelable{

    private String sender;
    private String subject;
    private String text;
    private String classification;

    public Mail(String sender, String subject, String text, String classification) {
        this.sender = sender;
        this.subject = subject;
        this.text = text;
        this.classification = classification;
    }

    protected Mail(Parcel in) {
        sender = in.readString();
        subject = in.readString();
        text = in.readString();
        classification = in.readString();
    }

    public static final Creator<Mail> CREATOR = new Creator<Mail>() {
        @Override
        public Mail createFromParcel(Parcel in) {
            return new Mail(in);
        }

        @Override
        public Mail[] newArray(int size) {
            return new Mail[size];
        }
    };

    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public boolean isSpam() {
        if(classification.equalsIgnoreCase("SPAM"))
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return sender + " " + subject + " " + text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sender);
        parcel.writeString(subject);
        parcel.writeString(text);
        parcel.writeString(classification);
    }
}
