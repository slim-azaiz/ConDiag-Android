package info.sagemcom.conDiag.model;


import info.sagemcom.conDiag.R;

/**
 * Created by Ravi Tamada on 21/02/17.
 * www.androidhive.info
 */

public class Server {
    private int id;
    private String ipAddress;
    private String friendlyName;
    private String model;
    private String timestamp;
    private String picture;
    private boolean isImportant;
    private boolean isRead;
    private int color = -1;

    public Server() {
    }
    public Server(int id, String ipAddress, String friendlyName, String model, String timestamp, String picture, boolean isImportant, boolean isRead, int color ) {
        this.id =id;
        this.ipAddress = ipAddress;
        this.friendlyName = friendlyName;
        this.model = model;
        this.timestamp = timestamp;
        this.picture = picture;
        this.isImportant = isImportant;
        this.isRead = isRead;
        this.color = color;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public String getPicture() {
        return "mipmap://" + R.mipmap.google;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
