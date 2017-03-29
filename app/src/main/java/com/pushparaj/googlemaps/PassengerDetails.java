package com.pushparaj.googlemaps;

class PassengerDetails {
    private String qr,airno,fromplace,toplace,ticketno,classes,purchase,name,airname,departure,gate;
    private int billnumber,ticketstatus;
    private double price;

    public String getAirname() {
        return airname;
    }

    public void setAirname(String airname) {
        this.airname = airname;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBillnumber() {
        return billnumber;
    }

    public int getTicketstatus() {
        return ticketstatus;
    }

    public String getAirno() {
        return airno;
    }

    public String getClasses() {
        return classes;
    }

    public String getFromplace() {
        return fromplace;
    }

    public String getPurchase() {
        return purchase;
    }

    public String getQr() {
        return qr;
    }

    public String getTicketno() {
        return ticketno;
    }

    public String getToplace() {
        return toplace;
    }

    public void setAirno(String airno) {
        this.airno = airno;
    }

    public void setBillnumber(int billnumber) {
        this.billnumber = billnumber;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public void setFromplace(String fromplace) {
        this.fromplace = fromplace;
    }

    public void setPurchase(String purchase) {
        this.purchase = purchase;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public void setTicketno(String ticketno) {
        this.ticketno = ticketno;
    }

    public void setTicketstatus(int ticketstatus) {
        this.ticketstatus = ticketstatus;
    }

    public void setToplace(String toplace) {
        this.toplace = toplace;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

}
