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

        ServerSocket serverSocket = new ServerSocket(1234);

        System.out.println("Le serveur est ouvert\n" + serverSocket.toString());

        Socket test = serverSocket.accept();

        System.out.println("La connexion est établie\n" + test.toString());

        InputStream input = test.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        // String Lecture = reader.readLine();


        String message = reader.readLine();
        System.out.println("Message reçu : " + message);

        test.close();
        serverSocket.close();




    }
}