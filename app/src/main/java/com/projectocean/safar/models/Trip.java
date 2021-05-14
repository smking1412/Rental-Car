package com.projectocean.safar.models;

import java.io.Serializable;

public class Trip implements Serializable {
    private String numberPlate,date,time,pul, carId,status, tripId,uid;
    private Integer rent,hours;
    private Long ts;
    private CardData cardInfo;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Trip(String carId, String date, String time, String pul, Integer rent
            , String numberPlate, Integer hours, String status, String tripId, Long ts) {
        this.numberPlate = numberPlate;
        this.date = date;
        this.time = time;
        this.pul = pul;
        this.rent=rent;
        this.carId = carId;
        this.hours=hours;
        this.status=status;
        this.tripId = tripId;
        this.ts=ts;
    }

    public CardData getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CardData cardInfo) {
        this.cardInfo = cardInfo;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPul(String pul) {
        this.pul = pul;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public void setRent(Integer rent) {
        this.rent = rent;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Long getTs() {
        return ts;
    }

    public String getTripId() {
        return tripId;
    }

    public String getStatus() {
        return status;
    }

    public Integer getRent() {
        return rent;
    }

    public String getCarId() {
        return carId;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPul() {
        return pul;
    }

    public Integer getHours() {
        return hours;
    }

    public Trip(){

    }
}
