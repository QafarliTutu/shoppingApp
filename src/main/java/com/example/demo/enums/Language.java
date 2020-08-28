package com.example.demo.enums;

public enum Language {

    AZ("AZ"),
    EN("EN"),
    RU("RU");

    private String value;

    Language(String value) {
    }

    public String getValue() {
        return value;
    }
}