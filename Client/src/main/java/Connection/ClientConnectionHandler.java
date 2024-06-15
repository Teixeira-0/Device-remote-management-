package Connection;

import Authentication.ClientTLSProvider;
import Session.ClientSession;
import Settings.ClientApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.net.ssl.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;


@Component
public class ClientConnectionHandler {

    //private final ServerSocket agentSocket;
    private final SSLSocketFactory serverSocketFactory;

    private final LinkedBlockingQueue<ClientSession> sessionPool = new LinkedBlockingQueue<ClientSession>();

    private final HashMap<Integer, ClientSession> sessionsMap = new HashMap<Integer, ClientSession>();

    private ExecutorService threadPool;

    private final Logger logger = Logger.getLogger(ClientConnectionHandler.class.getName());

    @Autowired
    public ClientConnectionHandler (ClientTLSProvider tlsProvider){
        this.serverSocketFactory = tlsProvider.tlsConnection();

        // The number of threads must be equal or superior to the number of sessions in the pool
        this.threadPool = Executors.newFixedThreadPool(ClientApplication.settings().getSessionPoolSize());

        //Pre-Populate session Pool to reduce overhead
        this.prePopulateSessionPool();
    }

    public void handleConnectionRequest() {
        try{
            while(true){
                SSLSocket sslSocket;

                Scanner s = new Scanner(System.in);

                int port = s.nextInt();

                sslSocket = (SSLSocket) serverSocketFactory.createSocket("localhost",port);
                sslSocket.startHandshake();

                SSLSession session = sslSocket.getSession();
                if (session.isValid()) {
                    this.establishSession(sslSocket);
                    logger.info("Handshake completed successfully with " + sslSocket.getInetAddress());
                } else {
                    logger.warning("Handshake failed or session is not valid with " + sslSocket.getInetAddress());
                    sslSocket.close();
                }
            }

        }catch (Exception e){
            //this.agentSocket.close();
            throw new Error("Connection failed, Error:" + e.getMessage());
        }
    }

    private void establishSession(SSLSocket connectionSocket) throws InterruptedException {

        ClientSession session = sessionPool.take();
        session.establishSocket(connectionSocket);
        sessionsMap.put(session.getSESSION_ID(),session);

        //session.downloadThread.start();
        //threadPool.submit(session);
    }

    private void prePopulateSessionPool(){
        for (int i = 0; i < ClientApplication.settings().getSessionPoolSize(); i ++){
            ClientSession session = new ClientSession();
            sessionPool.add(session);
        }
    }

    @GetMapping("/hello")
    public String testRouting (@RequestParam("ids") List<Integer> ids){

        ClientSession session;

        for (Integer id: ids) {
            session = sessionsMap.get(id);
            session.downloadThread.start();
        }

        return "FUNCIONOU :)";
    }


}
