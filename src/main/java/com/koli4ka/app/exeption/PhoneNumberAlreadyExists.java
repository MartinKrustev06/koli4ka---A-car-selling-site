package com.koli4ka.app.exeption;

public class PhoneNumberAlreadyExists extends RuntimeException {
    public PhoneNumberAlreadyExists(String message) {
        super(message);
    }
}