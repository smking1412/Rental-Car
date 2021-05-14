package com.projectocean.safar.models;

public class User {
    private String name,email,phone,currentCar;
    private Integer wallet,trips;
    private Boolean isRenting;
    public User(String name, String email, String phone, Boolean isRenting, String currentCar, Integer wallet, Integer trips){
        this.name=name;
        this.email=email;
        this.phone=phone;
        this.isRenting=isRenting;
        this.wallet=wallet;
        this.currentCar=currentCar;
        this.trips=trips;
    }

    public  User(){}

    public Integer getTrips() {
        return trips;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Boolean getIsRenting() {
        return isRenting;
    }

    public String getCurrentCar() {
        return currentCar;
    }

    public Integer getWallet() {
        return wallet;
    }


    public void setTrips(Integer trips) {
        this.trips = trips;
    }

    public void setIsRenting(Boolean isRenting) {
        this.isRenting = isRenting;
    }

    public void setCurrentCar(String currentCar) {
        this.currentCar = currentCar;
    }

    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
