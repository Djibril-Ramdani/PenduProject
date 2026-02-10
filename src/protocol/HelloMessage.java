package protocol;


import java.io.BufferedReader;
import java.io.IOException;

public class HelloMessage {

    String adresseIp;
    int numeroPort;

    public HelloMessage(String adresseIp, int numeroPort){
       this.adresseIp = adresseIp;
       this.numeroPort = numeroPort;
    }

    public Boolean verificationHello(){
        if(this.adresseIp == null || this.adresseIp.isEmpty()){
            return false;
        }else if(this.numeroPort ==2025){
            return false;
        }
        return true;
    }

    public String envoyerHello(){
        return "HELLO \n" + adresseIp + "\n" + numeroPort;
    }

    public HelloMessage(BufferedReader reader) throws IOException {
        BufferedReader input = new BufferedReader(reader);
        String line;
        line = input.readLine();
        adresseIp = line;
        line = input.readLine();
        numeroPort = Integer.parseInt(line);
        input.close();
        Boolean conforme = verificationHello();
    }
}