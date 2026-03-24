package com.chrionline.common;

import java.io.Serializable; // 1. NOUVEAU : On importe Serializable
import java.util.ArrayList;
import java.util.List;

// 2. NOUVEAU : On ajoute "implements Serializable" à la classe
public class Cart implements Serializable {
    // Attributs
    private int idPanier; 
    private List<LigneCart> lignes; 
    private double total; 

    // Constructeur
    public Cart(int idPanier) {
        this.idPanier = idPanier;
        this.lignes = new ArrayList<>(); 
        this.total = 0.0;
    }

    // Méthodes
    public String addproduct(Product p, int quantite) {
        if (p.verifieStock(quantite) == false) {
            return "ÉCHEC : Stock insuffisant pour " + p.getName() + " (Stock restant : " + p.getStock() + ")";
        }

        for (LigneCart ligne : lignes) {
            if (ligne.getProduct().getId() == p.getId()) {
                ligne.addQuantity(quantite); 
                p.updateStock(quantite); 
                calculatetotale(); 
                return "SUCCÈS : Quantité de " + p.getName() + " mise à jour dans le panier.";
            }
        }

        LigneCart nouvelleLigne = new LigneCart(p, quantite);
        lignes.add(nouvelleLigne); 
        p.updateStock(quantite); 
        calculatetotale(); 
        return "SUCCÈS : " + p.getName() + " ajouté au panier.";
    }

    public double calculatetotale() {
        this.total = 0.0; 
        for (LigneCart ligne : lignes) { 
            this.total = this.total + ligne.calculateSousTotal(); 
        }
        return this.total;
    }

    public String removeproduct(Product p) {
        LigneCart ligneASupprimer = null;
        for (LigneCart ligne : lignes) {
            if (ligne.getProduct().getId() == p.getId()) {
                ligneASupprimer = ligne;
                break; 
            }
        }

        if (ligneASupprimer != null) {
            lignes.remove(ligneASupprimer); 
            calculatetotale(); 
            return "SUCCÈS : Produit retiré du panier.";
        } else {
            return "ÉCHEC : Ce produit n'est pas dans votre panier.";
        }
    }

    public String emptycart() {
        this.lignes.clear(); 
        this.total = 0.0;
        return "Le panier a été vidé.";
    }
}