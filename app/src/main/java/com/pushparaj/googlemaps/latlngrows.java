package com.pushparaj.googlemaps;


public class latlngrows {
    double lat,lng;
    String name,offer;
    int icon,shop;

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public int getShop() {
        return shop;
    }

    public latlngrows(double lat, double lng, String name, int icon) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.icon = icon;
    }

    public void setShop(int shop) {
        this.shop = shop;
    }

    public latlngrows(int icon, double lat, double lng, String name, String offer, int shop) {

        this.icon = icon;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.offer = offer;
        this.shop = shop;
    }

    public latlngrows() {
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
