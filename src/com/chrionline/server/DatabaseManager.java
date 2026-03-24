package com.chrionline.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.chrionline.common.User;
import com.chrionline.common.Product;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:chrionline.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void setup() {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "login TEXT UNIQUE, "
                + "password TEXT);";

        String sqlProduits = "CREATE TABLE IF NOT EXISTS produits ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " name TEXT NOT NULL,"
                + " price REAL NOT NULL,"
                + " description TEXT,"
                + " stock INTEGER NOT NULL,"
                + " categorie TEXT"
                + ");";

        // NOUVEAU : La table pour sauvegarder les commandes
        String sqlCommandes = "CREATE TABLE IF NOT EXISTS commandes ("
                + " orderId TEXT PRIMARY KEY,"
                + " login TEXT NOT NULL,"
                + " total REAL NOT NULL,"
                + " date_cmd DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsers);
            stmt.execute(sqlProduits);
            stmt.execute(sqlCommandes); // NOUVEAU : On crée la table commandes
            System.out.println("Tables 'users', 'produits' et 'commandes' prêtes !");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM produits");
            if (rs.next() && rs.getInt("count") == 0) {
                
                String[][] vraisProduits = {
                    {"PC Gamer ASUS ROG", "15000.0", "PC portable haute performance", "Informatique"},
                    {"MacBook Pro M2", "22000.0", "Laptop Apple", "Informatique"},
                    {"Souris Logitech G502", "800.0", "Souris gamer 25K DPI", "Accessoires"},
                    {"Clavier Razer BlackWidow", "1200.0", "Clavier mécanique", "Accessoires"},
                    {"Ecran MSI Optix 27", "3500.0", "Ecran incurvé 144Hz", "Ecrans"},
                    {"Casque HyperX Cloud II", "900.0", "Casque gaming son 7.1", "Audio"},
                    {"Tapis de souris SteelSeries", "250.0", "Tapis XXL", "Accessoires"},
                    {"Disque Dur WD 2To", "700.0", "Disque dur externe USB 3.0", "Stockage"},
                    {"SSD Samsung 980 PRO 1To", "1100.0", "SSD NVMe ultra rapide", "Stockage"},
                    {"RAM Corsair 16Go", "650.0", "2x8Go DDR4", "Composants"},
                    {"Carte Graphique RTX 4070", "7500.0", "NVIDIA GeForce", "Composants"},
                    {"Processeur AMD Ryzen 7", "3200.0", "Processeur 8 coeurs", "Composants"},
                    {"Carte Mère MSI B550", "1400.0", "Carte mère ATX", "Composants"},
                    {"Boîtier PC Corsair", "1100.0", "Boîtier moyen tour", "Composants"},
                    {"Alimentation Seasonic", "1000.0", "Alimentation 750W", "Composants"},
                    {"Clé USB SanDisk 128Go", "150.0", "Clé USB 3.2", "Stockage"},
                    {"Imprimante HP Envy", "850.0", "Imprimante Wi-Fi", "Périphériques"},
                    {"Webcam Logitech C920", "600.0", "Webcam Full HD", "Périphériques"},
                    {"Micro Blue Yeti", "1300.0", "Microphone USB", "Audio"},
                    {"Enceintes PC Bose", "2500.0", "Système audio 2.0", "Audio"},
                    {"PC Bureau Dell", "6000.0", "PC pour entreprises", "Informatique"},
                    {"Ecran Samsung Odyssey", "5500.0", "Ecran gaming 240Hz", "Ecrans"},
                    {"Souris MX Master 3", "1100.0", "Souris sans fil", "Accessoires"},
                    {"Clavier MX Keys", "1200.0", "Clavier sans fil", "Accessoires"},
                    {"Casque Sony WH-1000XM5", "3800.0", "Réduction de bruit", "Audio"},
                    {"iPad Pro 11", "11000.0", "Tablette Apple", "Tablettes"},
                    {"Galaxy Tab S8", "9000.0", "Tablette Android", "Tablettes"},
                    {"iPhone 15 Pro", "14000.0", "Smartphone 256Go", "Téléphones"},
                    {"Samsung S23 Ultra", "13500.0", "Smartphone Premium", "Téléphones"},
                    {"Apple Watch 9", "4500.0", "Montre connectée", "Objets Connectés"},
                    {"Routeur Wi-Fi 6 TP-Link", "800.0", "Routeur haut débit", "Réseaux"},
                    {"Répéteur Wi-Fi Netgear", "400.0", "Amplificateur", "Réseaux"},
                    {"Câble HDMI 2.1", "150.0", "Câble tressé 8K", "Câbles"},
                    {"Chargeur Rapide Anker", "200.0", "Chargeur USB-C 65W", "Accessoires"},
                    {"Batterie Externe 20000mAh", "350.0", "Powerbank", "Accessoires"},
                    {"Manette Xbox Series", "650.0", "Manette sans fil", "Gaming"},
                    {"Manette PS5 DualSense", "750.0", "Manette Sony", "Gaming"},
                    {"Chaise Gamer Secretlab", "4500.0", "Chaise ergonomique", "Mobilier"},
                    {"Bureau Gamer", "2200.0", "Bureau avec LED", "Mobilier"},
                    {"Onduleur APC 1500VA", "1800.0", "Protection coupures", "Protection"},
                    {"Pâte Thermique", "100.0", "Haute performance", "Composants"},
                    {"Ventilateur Be Quiet", "200.0", "Ventilateur silencieux", "Composants"},
                    {"Watercooling Corsair", "1500.0", "Refroidissement liquide", "Composants"},
                    {"Carte Son Sound Blaster", "800.0", "Carte interne 5.1", "Composants"},
                    {"Adaptateur Bluetooth", "100.0", "Dongle USB 5.0", "Réseaux"},
                    {"Hub USB-C UGREEN", "300.0", "Hub 6 en 1", "Accessoires"},
                    {"Support Ecran", "1400.0", "Bras articulé", "Mobilier"},
                    {"Disque NAS Seagate 4To", "1200.0", "Disque serveur", "Stockage"},
                    {"Serveur NAS Synology", "3000.0", "Boîtier 2 baies", "Réseaux"},
                    {"Lunettes Anti-lumière bleue", "400.0", "Protection écrans", "Accessoires"}
                };
                
                String sqlInsert = "INSERT INTO produits (name, price, description, stock, categorie) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    for (String[] prod : vraisProduits) {
                        pstmt.setString(1, prod[0]); 
                        pstmt.setDouble(2, Double.parseDouble(prod[1])); 
                        pstmt.setString(3, prod[2]); 
                        pstmt.setInt(4, 15); 
                        pstmt.setString(5, prod[3]); 
                        pstmt.executeUpdate(); 
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'initialisation : " + e.getMessage());
        }
    }

    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users(login, password) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getLogin());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean checkLogin(String login, String password) {
        String sql = "SELECT * FROM users WHERE login = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); 
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean updateUserPassword(String login, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE login = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, login);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean deleteUser(String login) {
        String sql = "DELETE FROM users WHERE login = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static List<Product> getAllProducts() {
        List<Product> catalogue = new ArrayList<>();
        String sql = "SELECT * FROM produits"; 
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) { 
            while (rs.next()) {
                Product p = new Product(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"),
                        rs.getString("description"), rs.getInt("stock"), rs.getString("categorie"));
                catalogue.add(p); 
            }
        } catch (SQLException e) {
            System.out.println("Erreur de lecture produits : " + e.getMessage());
        }
        return catalogue; 
    }

    // ==========================================
    // NOUVEAU : ENREGISTREMENT DE LA COMMANDE
    // ==========================================
    public static boolean saveOrder(String orderId, String login, double total) {
        String sql = "INSERT INTO commandes(orderId, login, total) VALUES(?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            pstmt.setString(2, login);
            pstmt.setDouble(3, total);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur d'enregistrement de commande : " + e.getMessage());
            return false;
        }
    }
}