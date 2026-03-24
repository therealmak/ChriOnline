package com.chrionline.common;

import java.io.Serializable; // 1. NOUVEAU : On importe Serializable
import com.chrionline.common.Product;

// 2. NOUVEAU : On ajoute "implements Serializable" à la classe
public class LigneCart implements Serializable {
    // Attributs
    private Product product; 
    private int quantity; 
    private double Total; 

    // Constructeur
    public LigneCart(Product product, int quantity) { 
        this.product = product;
        this.quantity = quantity;
        this.Total = calculateSousTotal(); 
    }

    // Méthodes
    public double calculateSousTotal() {
        return this.product.getPrice() * this.quantity;
    }

    // Getters et Setters
    public Product getProduct() { return product; } 
    
    public int getQuantity() { return quantity; }
    
    public void addQuantity(int extraQuantity) {
        this.quantity = this.quantity + extraQuantity;
        this.Total = calculateSousTotal(); 
    }

    public double getTotal() { return Total; }
}