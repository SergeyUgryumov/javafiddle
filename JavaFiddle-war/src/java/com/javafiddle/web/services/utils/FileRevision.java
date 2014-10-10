package com.javafiddle.web.services.utils;
/**
 * Contains two fields: <i>timeStamp</i> and <i>value</i>. <br/>
 * And all getters and setters for them.
 */
public class FileRevision {
    long timeStamp;
    String value;

    public FileRevision() {
    }

    public FileRevision(long timeStamp, String value) {
        this.timeStamp = timeStamp;
        this.value = value;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long time) {
        this.timeStamp = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String valueStamp) {
        this.value = valueStamp;
    }
}
