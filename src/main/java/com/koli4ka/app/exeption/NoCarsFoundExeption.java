package com.koli4ka.app.exeption;

public class NoCarsFoundExeption extends RuntimeException {
    public NoCarsFoundExeption(String message) {
        super(message);
    }

}
