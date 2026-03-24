package com.chrionline.common;

import java.io.Serializable; // 1. NOUVEAU : On importe Serializable

// 2. NOUVEAU : On ajoute "implements Serializable" à la classe
public class Product implements Serializable { 
    // Attributs
    private int id; 
    private String name; 
    private double price; 
    private String description; 
    private int stock; 
    private String categorie; 

    // Constructeur
    public Product(int id, String name, double price, String description, int stock, String categorie) { 
        this.id = id; 
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.categorie = categorie;
    }

    // Méthodes
    public String getDetails() {
        return "ID: " + id + " | " + name + " | " + price + " MAD | Stock: " + stock + " | " + description;
    }

    public boolean verifieStock(int qte) {
        if (this.stock >= qte) {
            return true; 
        } else {
            return false; 
        }
    }

    public void updateStock(int qte) {
        this.stock = this.stock - qte; 
    }

    // Getters 
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
}