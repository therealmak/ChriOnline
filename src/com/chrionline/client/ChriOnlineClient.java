package com.chrionline.client;

import com.chrionline.common.User;
import com.chrionline.common.Product; 
import java.io.*;
import java.net.Socket;
import java.util.List; 
import java.util.Scanner;

// IMPORTS POUR LA VALIDATION STRICTE DES DATES
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ChriOnlineClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("--- Connecté au Serveur ChriOnline ---");

            // --- 1. INSCRIPTION ---
            System.out.println("\n--- 1. Inscription ---");
            System.out.print("Choisir un pseudo : ");
            String loginReg = scanner.nextLine();
            System.out.print("Choisir un mot de passe : ");
            String passReg = scanner.nextLine();

            out.writeObject("REGISTER");
            out.writeObject(new User(loginReg, passReg));
            out.flush();

            String regResponse = (String) in.readObject();
            System.out.println("Résultat Inscription : " + regResponse);

            // --- 2. CONNEXION ---
            System.out.println("\n--- 2. Connexion ---");
            System.out.print("Entrez votre login : ");
            String logIn = scanner.nextLine();
            System.out.print("Entrez votre mot de passe : ");
            String passIn = scanner.nextLine();

            out.writeObject("LOGIN");
            out.writeObject(logIn);
            out.writeObject(passIn);
            out.flush();

            String authResponse = (String) in.readObject();
            
            if (authResponse.equals("AUTH_OK")) {
                System.out.println("Bravo ! Connexion réussie.\n");

                boolean inStore = true;
                while (inStore) {
                    System.out.println("\n🛒 === MENU BOUTIQUE CHRIONLINE === 🛒");
                    System.out.println("1. Voir le catalogue des produits");
                    System.out.println("2. Ajouter un produit au panier");
                    System.out.println("3. Retirer un produit du panier");
                    System.out.println("4. Voir le total à payer");
                    System.out.println("5. Payer et valider la commande"); 
                    System.out.println("6. Quitter la boutique (Déconnexion)"); 
                    System.out.print("Votre choix (1-6) : ");
                    
                    String choix = scanner.nextLine();

                    switch (choix) {
                        case "1":
                            out.writeObject("GET_CATALOGUE");
                            out.flush();
                            @SuppressWarnings("unchecked")
                            List<Product> catalogue = (List<Product>) in.readObject();
                            System.out.println("\n--- NOTRE CATALOGUE ---");
                            for (Product p : catalogue) {
                                System.out.println(p.getDetails());
                            }
                            break;

                        case "2":
                            System.out.print("ID du produit : ");
                            int idAdd = Integer.parseInt(scanner.nextLine());
                            System.out.print("Quantité : ");
                            int qtyAdd = Integer.parseInt(scanner.nextLine());
                            out.writeObject("ADD_TO_CART");
                            out.writeObject(idAdd);
                            out.writeObject(qtyAdd);
                            out.flush();
                            System.out.println(">> " + in.readObject());
                            break;

                        case "3":
                            System.out.print("ID du produit à retirer : ");
                            int idRemove = Integer.parseInt(scanner.nextLine());
                            out.writeObject("REMOVE_FROM_CART");
                            out.writeObject(idRemove);
                            out.flush();
                            System.out.println(">> " + in.readObject());
                            break;

                        case "4":
                            out.writeObject("VIEW_TOTAL");
                            out.flush();
                            double total = (Double) in.readObject();
                            System.out.println(">> TOTAL ACTUEL DU PANIER : " + total + " MAD");
                            break;

                        case "5":
                            System.out.println("\n--- MÉTHODE DE PAIEMENT ---");
                            System.out.println("1. Paiement à la livraison (Espèces)");
                            System.out.println("2. Paiement par Carte Bancaire");
                            System.out.print("Choisissez (1-2) : ");
                            String choixPaiement = scanner.nextLine();

                            if (choixPaiement.equals("1")) {
                                out.writeObject("PAY_DELIVERY");
                                out.flush();
                                System.out.println("\n>> " + in.readObject() + "\n");
                            } else if (choixPaiement.equals("2")) {
                                System.out.print("Numéro de carte (16 chiffres) : ");
                                String numCarte = scanner.nextLine();
                                System.out.print("Date d'expiration (MM/YY) : ");
                                String dateExp = scanner.nextLine();
                                System.out.print("Code CVV (3 chiffres) : ");
                                String cvv = scanner.nextLine();

                                // --- VALIDATION LOCALE (CLIENT) ---
                                boolean isNumValide = numCarte.matches("\\d{16}");
                                boolean isCvvValide = cvv.matches("\\d{3}");
                                boolean isDateValide = false;

                                try {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
                                    YearMonth dateExpiration = YearMonth.parse(dateExp, formatter);
                                    
                                    if (!dateExpiration.isBefore(YearMonth.now())) {
                                        isDateValide = true;
                                    } else {
                                        System.out.println("\n❌ ERREUR : Carte expirée (" + dateExp + ").");
                                    }
                                } catch (DateTimeParseException e) {
                                    System.out.println("\n❌ ERREUR : La date '" + dateExp + "' est invalide (ex: mois 14 ou format faux).");
                                }

                                if (isNumValide && isDateValide && isCvvValide) {
                                    out.writeObject("PAY_CARD");
                                    out.flush();
                                    System.out.println("\n>> " + in.readObject() + "\n");
                                } else {
                                    System.out.println("❌ PAIEMENT REFUSÉ : Saisie incorrecte.");
                                }
                            }
                            break;

                        case "6":
                            inStore = false; 
                            break;

                        default:
                            System.out.println("Choix invalide.");
                    }
                }
            } else {
                System.out.println("Identifiants incorrects.");
            }

            out.writeObject("LOGOUT");
            System.out.println("\n--- Déconnexion réussie ---");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur : " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erreur : Veuillez saisir des nombres valides !");
        }
    }
}