package Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Session implements Runnable {

    private Socket sessionSocket;
    private static int SESSION_ID_GENERATOR = 0;
    private final int SESSION_ID;

    public Session () {
        this.SESSION_ID = SESSION_ID_GENERATOR + 1;
        SESSION_ID_GENERATOR ++;
    }


    public void establishSocket (Socket agentSocket){
        this.sessionSocket = agentSocket;
    }
    

    @Override
    public void run() {

        String line="";
        Scanner scanner = new Scanner(System.in);

        System.out.println("PROMPT: ");
        //line = scanner.nextLine();

        while(!Objects.equals(line, "quit")){
            System.out.println(this.SESSION_ID);
            System.out.println("WORKED");
            try {
                InputStream i = this.sessionSocket.getInputStream();
                OutputStream o = this.sessionSocket.getOutputStream();

                o.write("hmmm hmm".getBytes());

                byte[] buffer = new byte[1024];
                int bytesRead = i.read(buffer);
                System.out.println("Received: " + new String(buffer, 0, bytesRead));

                System.out.println("PROMPT: ");
                //line = scanner.nextLine();

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        try {
            this.sessionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
