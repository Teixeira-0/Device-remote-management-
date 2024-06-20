package Session;


import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLSocket;


@RestController
public class Session implements Runnable {

    private SSLSocket sessionSocket;
    private static int SESSION_ID_GENERATOR = 0;
    private final int SESSION_ID;


    public int getSESSION_ID() {
        return SESSION_ID;
    }

    public Session () {
        this.SESSION_ID = SESSION_ID_GENERATOR + 1;
        SESSION_ID_GENERATOR ++;
    }


    public void establishSocket (SSLSocket agentSocket){
        this.sessionSocket = agentSocket;
    }

    @Override
    public void run() {
        System.out.println("THREAD: " + Thread.currentThread().getName());


        while(true){

        }

    }
}
