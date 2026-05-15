package com.example.kazstorebot;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "menu")
public class MenuItem {
    public enum Category {
        PIZZA, BURGER, SUSHI, WINGS, FRIES, SAUCES, DRINK, DESSERT
    }

    public enum Size {
        SMALL, MEDIUM, LARGE, EXTRALARGE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    public Long id;
    @Getter
    public String name;
    @Getter
    public int price;
    @Enumerated(EnumType.STRING)
    public Category category;
    @Enumerated(EnumType.STRING)
    @Getter
    public Size size;

    public MenuItem(Long id, String name, int price, Category category, Size size) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.size = size;
    }

    public MenuItem() {}

}
