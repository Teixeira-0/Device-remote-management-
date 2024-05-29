package Connection;

import Session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class ConnectionHandler {

    private static final String SESSION_POOL_SIZE = "SessionPoolSize ";
    private final ServerSocket agentSocket;

    private final LinkedBlockingQueue<Session> sessionPool = new LinkedBlockingQueue<Session>();
    private ExecutorService threadPool;

    @Autowired
    public ConnectionHandler(ConnectionProvider connectionProvider){
        this.agentSocket = connectionProvider.getServerSocket();
        this.threadPool = Executors.newFixedThreadPool(50);
    }

    public void handleConnectionRequest() throws IOException {

        try{
            while(true){
                Socket socket = this.agentSocket.accept();

            }

        }catch (Exception e){
            this.agentSocket.close();
            throw new Error("Connection failed, Error:" + e.getMessage());
        }

    }

    public void establishSession(Socket connectionSockets) throws InterruptedException {


    }

    private void prePopulateSessionPool(){
        for (int i = 0; i < 12; i ++){

        }
    }

}
