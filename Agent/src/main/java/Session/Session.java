package Session;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@RestController
public class Session implements Runnable {

    private Socket sessionSocket;
    private static int SESSION_ID_GENERATOR = 0;
    private final int SESSION_ID;

    public Thread downloadThread;

    public int getSESSION_ID() {
        return SESSION_ID;
    }

    public Session () {
        this.SESSION_ID = SESSION_ID_GENERATOR + 1;
        SESSION_ID_GENERATOR ++;
        // Call the download method inside the new thread
        downloadThread = new Thread(this::test);
    }


    public void establishSocket (Socket agentSocket){
        this.sessionSocket = agentSocket;
    }

    public void test (){
        System.out.println("THREAD: " + Thread.currentThread().getName());
        //System.out.println(this.sessionSocket);
        System.out.println("Parece que funciona, és um génio DIDI");
    }

    @Override
    public void run() {
        System.out.println("THREAD: " + Thread.currentThread().getName());
        String line="";
        Scanner scanner = new Scanner(System.in);

        System.out.println("PROMPT: ");
        //line = scanner.nextLine();

        while(!Objects.equals(line, "quit")){

        }

        try {
            this.sessionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
