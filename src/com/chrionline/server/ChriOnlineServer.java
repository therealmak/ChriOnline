package com.chrionline.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChriOnlineServer {

    public static void main(String[] args) {
        int port = 1234; // Le port sur lequel le serveur écoute (doit être le même que dans le Client)

        // Avant de lancer le serveur, on initialise la base de données SQLite
        System.out.println("Initialisation de la base de données...");
        DatabaseManager.setup();

        // TÂCHE 1 : Créer le ServerSocket
        // Le "try-with-resources" permet de fermer le serveur proprement en cas de crash
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            
            System.out.println("\n=== Serveur ChriOnline Démarré ===");
            System.out.println("En attente de connexion des clients sur le port " + port + "...");

            // Boucle infinie : le serveur tourne en permanence pour attendre des clients
            while (true) {
                
                // TÂCHE 2 : Accepter les clients
                // Le programme se met en pause ici (bloquant) jusqu'à ce qu'un client se connecte
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n[+] Nouveau client connecté depuis l'adresse : " + clientSocket.getInetAddress());

                // TÂCHE 3 & 5 : Gérer plusieurs clients avec Threads et gérer les requêtes
                // On donne le socket du client à notre classe ClientHandler (qui contient le switch avec ADD_TO_CART, PAY...)
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                
                // On crée un nouveau Thread (un nouveau fil d'exécution) pour ce client
                Thread clientThread = new Thread(clientHandler);
                
                // On lance le Thread. Le serveur peut maintenant retourner immédiatement au "accept()" pour le client suivant !
                clientThread.start();
            }

        } catch (IOException e) {
            System.err.println("Erreur fatale du serveur : " + e.getMessage());
        }
    }
}