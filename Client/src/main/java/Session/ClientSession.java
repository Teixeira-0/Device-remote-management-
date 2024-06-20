package Session;

import javax.net.ssl.SSLSocket;

public class ClientSession {
    private SSLSocket sessionSocket;
    private static int SESSION_ID_GENERATOR = 0;
    private final int SESSION_ID;

    public Thread downloadThread;

    public int getSESSION_ID() {
        return SESSION_ID;
    }

    public ClientSession () {
        this.SESSION_ID = SESSION_ID_GENERATOR + 1;
        SESSION_ID_GENERATOR ++;

        // Call the download method inside the new thread
        //downloadThread = new Thread(this::test);
    }


    public void establishSocket (SSLSocket agentSocket){
        this.sessionSocket = agentSocket;
    }

}
