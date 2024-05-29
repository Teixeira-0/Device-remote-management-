package Session;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Session implements Runnable {

    private Socket sessionSocket;

    public Session () {

    }


    public void establishSocket (Socket agentSocket){
        this.sessionSocket = agentSocket;
    }

    @Override
    public void run() {
        System.out.println("WORKED");
        try {
            InputStream i = this.sessionSocket.getInputStream();
            System.out.println(i.read());
            if(i.read() == 49){
                System.out.println("PRIMEIRO");
            }

            if(i.read() == 50){
                System.out.println("SEGUNDO");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
