package com.projectocean.safar.models;

import java.io.Serializable;

public class Car implements Serializable {
    private String numberPlate, carModelName,availability,img,location,carId,search;
    private Integer capacity,perhr,base;

    public Car(String numberPlate, String carModelName, String availability, String img,
               String location, String carId, Integer capacity, Integer perhr, Integer base,String search) {
        this.numberPlate = numberPlate;
        this.carModelName = carModelName;
        this.availability = availability;
        this.img = img;
        this.location = location;
        this.carId = carId;
        this.capacity = capacity;
        this.perhr = perhr;
        this.base = base;
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getCarModelName() {
        return carModelName;
    }

    public void setCarModelName(String carModelName) {
        this.carModelName = carModelName;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getPerhr() {
        return perhr;
    }

    public void setPerhr(Integer perhr) {
        this.perhr = perhr;
    }

    public Integer getBase() {
        return base;
    }

    public void setBase(Integer base) {
        this.base = base;
    }

    public Car(){

    }
}
