package com.example.amore;

public class User {
    private String Name, Surname, PhoneNum;

    public User() {
    }

    public User(String name, String surname, String phoneNum) {
        Name = name;
        Surname = surname;
        PhoneNum = phoneNum;
    }


    public String getName() {
        return Name;
    }

    public String getSurname() {
        return Surname;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }
}
