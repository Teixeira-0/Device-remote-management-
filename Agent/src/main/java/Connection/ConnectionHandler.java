package Connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ConnectionHandler {

    private final ServerSocket agentSocket;

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

    public void serveClient(Socket connectionSockets) throws InterruptedException {


    }

}
