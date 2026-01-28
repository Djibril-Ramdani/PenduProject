package protocol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class HelloMessage {
    private String adresseIpMaitreDuJeu;
    private int adressePort;

    public HelloMessage(String adresseIpMaitreDuJeu, int adressePort) throws IOException {
        this.adresseIpMaitreDuJeu = adresseIpMaitreDuJeu;
        this.adressePort = adressePort;
        if (adresseIpMaitreDuJeu == null)
            System.out.println("L'adresse IP du maître du jeu est nulle");
        else if (adressePort == 2025 ) {
            System.out.println("Le port doit être différent de 2025");

        }

        Socket socket = new Socket("adresseIpGameMaster", adressePort);

        OutputStream output = socket.getOutputStream();

        BufferedWriter ecrivain = new BufferedWriter(new OutputStreamWriter(output));

        ecrivain.write("HELLO");
        ecrivain.newLine();
        ecrivain.flush();

    }
}
