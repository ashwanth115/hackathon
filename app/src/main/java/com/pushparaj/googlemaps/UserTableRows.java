package com.pushparaj.googlemaps;

/**
 * Created by ashwanth on 20/03/17.
 */
public class UserTableRows {
        String qr,name,pass,airport;
        int age,bill,usertype,sex;

    public int getAge() {
        return age;
    }

    public String getAirport() {
        return airport;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public int getBill() {
        return bill;
    }

    public int getSex() {
        return sex;
    }

    public String getName() {
        return name;
    }

    public int getUsertype() {
        return usertype;
    }

    public String getPass() {
        return pass;
    }

    public String getQr() {
        return qr;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setBill(int bill) {
        this.bill = bill;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }
}
