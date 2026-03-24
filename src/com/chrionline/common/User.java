package com.chrionline.common;

import java.io.Serializable;
public class User implements Serializable {
    
    // Identifiant unique pour la version de la classe 
    private static final long serialVersionUID = 1L;

    private int id; // Identifiant unique en base de données
    private String login;
    private String password;
    private String email; // Utile pour la gestion de profil complète

    // Constructeur vide 
    public User() {}

    // Constructeur pour l'inscription (sans ID car l'ID est auto-incrémenté par la base)
    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // Constructeur complet (utile pour la modification ou l'affichage du profil)
    public User(int id, String login, String password, String email) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    // Getters et Setters (indispensables pour accéder aux données)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "User [id=" + id + ", login=" + login + ", email=" + email + "]";
    }
}