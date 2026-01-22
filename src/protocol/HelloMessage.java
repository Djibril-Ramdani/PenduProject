package protocol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class HelloMessage {
    private String adresseIpGameMaster;
    private int adressePort;

    public HelloMessage(String adresseIpGameMaster, int adressePort) throws IOException {
        this.adresseIpGameMaster = adresseIpGameMaster;
        this.adressePort = adressePort;
        if (adresseIpGameMaster == null)
            System.out.println("Adresse IP Game Master is null");
        else if (adressePort == 2025 ) {
            System.out.println("Le port doit être différent de 2025");

        }

        Socket socket = new Socket("adresseIpGameMaster", adressePort);

        OutputStream output = socket.getOutputStream();

        BufferedWriter test = new BufferedWriter(new OutputStreamWriter(output));

        test.write("HELLO");
        test.newLine();
        test.flush();

    }
}
