package com.chrionline.server;

import com.chrionline.common.User;
import com.chrionline.common.Product;
import com.chrionline.common.Cart;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Cart clientCart = null;
    private String currentLogin = null;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                String action = (String) in.readObject();
                System.out.println("Action demandée par le client : " + action);

                switch (action) {
                    
                    case "REGISTER":
                        User newUser = (User) in.readObject();
                        boolean regOk = DatabaseManager.registerUser(newUser);
                        out.writeObject(regOk ? "REG_OK" : "REG_ERR");
                        break;

                    case "LOGIN":
                        String log = (String) in.readObject();
                        String pass = (String) in.readObject();
                        boolean authOk = DatabaseManager.checkLogin(log, pass);
                        
                        if (authOk) {
                            clientCart = new Cart(Math.abs(log.hashCode()));
                            currentLogin = log; 
                        }
                        
                        out.writeObject(authOk ? "AUTH_OK" : "AUTH_ERR");
                        break;

                    case "UPDATE_PASS":
                        String userLog = (String) in.readObject();
                        String newPass = (String) in.readObject();
                        boolean upOk = DatabaseManager.updateUserPassword(userLog, newPass);
                        out.writeObject(upOk ? "UPDATE_OK" : "UPDATE_ERR");
                        break;

                    case "DELETE":
                        String delLog = (String) in.readObject();
                        boolean delOk = DatabaseManager.deleteUser(delLog);
                        out.writeObject(delOk ? "DELETE_OK" : "DELETE_ERR");
                        break;

                    case "GET_CATALOGUE":
                        List<Product> catalogue = DatabaseManager.getAllProducts();
                        out.writeObject(catalogue); 
                        break;

                    case "ADD_TO_CART":
                        int idToAdd = (int) in.readObject();
                        int qtyToAdd = (int) in.readObject();
                        
                        if (clientCart != null) {
                            Product productToAdd = findProductById(idToAdd);
                            if (productToAdd != null) {
                                String msgAdd = clientCart.addproduct(productToAdd, qtyToAdd);
                                out.writeObject(msgAdd);
                            } else {
                                out.writeObject("ÉCHEC : Produit introuvable.");
                            }
                        } else {
                            out.writeObject("ÉCHEC : Vous devez être connecté pour avoir un panier.");
                        }
                        break;

                    case "REMOVE_FROM_CART":
                        int idToRemove = (int) in.readObject();
                        if (clientCart != null) {
                            Product productToRemove = findProductById(idToRemove);
                            if (productToRemove != null) {
                                String msgRemove = clientCart.removeproduct(productToRemove);
                                out.writeObject(msgRemove);
                            } else {
                                out.writeObject("ÉCHEC : Produit introuvable.");
                            }
                        }
                        break;

                    case "VIEW_TOTAL":
                        if (clientCart != null) {
                            out.writeObject(clientCart.calculatetotale());
                        } else {
                            out.writeObject(0.0);
                        }
                        break;

                    // ==========================================
                    // GESTION DU PAIEMENT (AVANCÉ)
                    // ==========================================
                    case "PAY_DELIVERY":
                    case "PAY_CARD":
                        if (clientCart != null && clientCart.calculatetotale() > 0) {
                            
                            double totalCmd = clientCart.calculatetotale();
                            String orderId = "CMD-" + System.currentTimeMillis();
                            
                            boolean isSaved = DatabaseManager.saveOrder(orderId, currentLogin, totalCmd);
                            
                            if (isSaved) {
                                clientCart.emptycart(); 
                                
                                if (action.equals("PAY_DELIVERY")) {
                                    out.writeObject("✅ SUCCÈS : Commande " + orderId + " validée ! Préparez " + totalCmd + " MAD en espèces pour le livreur.");
                                } else if (action.equals("PAY_CARD")) {
                                    out.writeObject("✅ SUCCÈS BANCAIRE : Paiement par carte accepté. Commande " + orderId + " validée ! Montant débité : " + totalCmd + " MAD.");
                                }
                            } else {
                                out.writeObject("❌ ÉCHEC : Erreur système lors de l'enregistrement de votre commande.");
                            }
                        } else {
                            out.writeObject("⚠️ IMPOSSIBLE : Votre panier est vide.");
                        }
                        break;

                    case "LOGOUT":
                        System.out.println("Client demande la déconnexion.");
                        return;
                }
                out.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Fin de connexion pour un client.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Product findProductById(int id) {
        List<Product> catalogue = DatabaseManager.getAllProducts();
        for (Product p : catalogue) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}