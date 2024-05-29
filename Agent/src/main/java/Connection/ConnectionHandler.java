package Connection;

import Session.Session;
import Settings.Application;
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

    private final ServerSocket agentSocket;

    private final LinkedBlockingQueue<Session> sessionPool = new LinkedBlockingQueue<Session>();
    private ExecutorService threadPool;

    @Autowired
    public ConnectionHandler(ConnectionProvider connectionProvider){
        this.agentSocket = connectionProvider.getServerSocket();

        // The number of threads must be equal or superior to the number of sessions in the pool
        this.threadPool = Executors.newFixedThreadPool(Application.settings().getSessionPoolSize());

        //Pre-Populate session Pool to reduce overhead
        this.prePopulateSessionPool();
    }

    public void handleConnectionRequest() throws IOException {

        try{
            while(true){
                Socket socket = this.agentSocket.accept();
                this.establishSession(socket);
            }

        }catch (Exception e){
            this.agentSocket.close();
            throw new Error("Connection failed, Error:" + e.getMessage());
        }

    }

    private void establishSession(Socket connectionSocket) throws InterruptedException {
        Session session = sessionPool.take();
        session.establishSocket(connectionSocket);
        threadPool.submit(session);
    }

    private void prePopulateSessionPool(){
        for (int i = 0; i < Application.settings().getSessionPoolSize(); i ++){
            Session session = new Session();
            sessionPool.add(session);
        }
    }

}
