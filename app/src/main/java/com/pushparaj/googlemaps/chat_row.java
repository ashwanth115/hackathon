package com.pushparaj.googlemaps;

/**
 * Created by Vijay on 26-03-2017.
 */

public class chat_row {
    String mess,name;
    int lor;

    public chat_row(String mess, String name,int lor) {
        this.mess = mess;
        this.name = name;
        this.lor = lor;
    }

    public chat_row() {
    }

    public int getLor() {
        return lor;
    }

    public void setLor(int lor) {
        this.lor = lor;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
