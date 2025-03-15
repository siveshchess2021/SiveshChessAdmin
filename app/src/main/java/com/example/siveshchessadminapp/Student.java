package com.example.siveshchessadminapp;

public class Student implements java.io.Serializable{
    private String studentId;
    private String name;
    private String parentName;
    private String mobileNumber1;
    private String mobileNumber2;
    private String parentOccupation;
    private String address;
    private String schoolName;
    private String dateOfBirth;
    private String emailId;
    private String photoUrl;

    public Student() {
        // Required empty constructor for Firebase
    }

    public Student(String name, String parentName, String mobileNumber1, String mobileNumber2, String parentOccupation, String address, String schoolName, String dateOfBirth, String emailId, String photoUrl) {
        this.name = name;
        this.parentName = parentName;
        this.mobileNumber1 = mobileNumber1;
        this.mobileNumber2 = mobileNumber2;
        this.parentOccupation = parentOccupation;
        this.address = address;
        this.schoolName = schoolName;
        this.dateOfBirth = dateOfBirth;
        this.emailId = emailId;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getMobileNumber1() {
        return mobileNumber1;
    }

    public void setMobileNumber1(String mobileNumber1) {
        this.mobileNumber1 = mobileNumber1;
    }

    public String getMobileNumber2() {
        return mobileNumber2;
    }

    public void setMobileNumber2(String mobileNumber2) {
        this.mobileNumber2 = mobileNumber2;
    }

    public String getParentOccupation() {
        return parentOccupation;
    }

    public void setParentOccupation(String parentOccupation) {
        this.parentOccupation = parentOccupation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}