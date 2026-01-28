package main;

import java.io.*;
import java.net.*;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class GameMaster {
    public static void main(String[] args) throws IOException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("");

        ServerSocket socketServeur = new ServerSocket(1234);

        System.out.println("Le serveur est ouvert\n" + socketServeur.toString());

        Socket socketClient = socketServeur.accept();

        System.out.println("La connexion est établie\n" + socketClient.toString());

        InputStream entree = socketClient.getInputStream();
        BufferedReader lecteur = new BufferedReader(new InputStreamReader(entree));

        // String Lecture = reader.readLine();


        String messageRecu = lecteur.readLine();
        System.out.println("Message reçu : " + messageRecu);

        socketClient.close();
        socketServeur.close();




    }
}
