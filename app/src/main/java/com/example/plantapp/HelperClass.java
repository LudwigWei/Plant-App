package com.example.plantapp;

// Helper class for Database
public class HelperClass {

    private String fullName, email, username, password, country, region, number, birthday, gender, interest;

    // Constructor
    public HelperClass(String fullName, String email, String username, String password,
                       String country, String region, String number, String birthday,
                       String gender, String interest) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.country = country;
        this.region = region;
        this.number = number;
        this.birthday = birthday; // Use birthday instead of separate day, month, year
        this.gender = gender;
        this.interest = interest;
    }

    // Empty constructor
    public HelperClass() {
    }

    // Getters and setters for all fields
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBirthday() {
        return birthday; // Updated to return birthday
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday; // Updated to set birthday
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }
}
