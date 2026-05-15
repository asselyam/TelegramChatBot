package com.example.kazstorebot;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

public class User {
    public enum UserState {
        NONE, WAITING_NAME, WAITING_ADDRESS, WAITING_PHONE;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
}
