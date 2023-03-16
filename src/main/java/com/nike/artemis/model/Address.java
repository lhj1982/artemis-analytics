package com.nike.artemis.model;




public class Address{
    public String streetAddr1;
    public String city;
    public String county;
    public String state;

    public String postalCode;
    public String country;

    public Address() {
    }

    public Address(String streetAddr1, String city, String county, String state, String postalCode, String country) {
        this.streetAddr1 = streetAddr1;
        this.city = city;
        this.county = county;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getStreetAddr1() {
        return streetAddr1;
    }

    public void setStreetAddr1(String streetAddr1) {
        this.streetAddr1 = streetAddr1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Address{" +
                "streetAddr1='" + streetAddr1 + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}