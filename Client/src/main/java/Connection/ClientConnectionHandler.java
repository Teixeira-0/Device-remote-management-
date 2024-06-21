package Connection;

import Authentication.ClientTLSProvider;
import Session.ClientSession;
import Settings.ClientApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.net.ssl.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;


@Component
public class ClientConnectionHandler {

    //private final ServerSocket agentSocket;
    private final SSLSocketFactory serverSocketFactory;

    private final LinkedBlockingQueue<ClientSession> sessionPool = new LinkedBlockingQueue<ClientSession>();

    private static final HashMap<Integer, ClientSession> sessionsMap = new HashMap<Integer, ClientSession>();

    private final Logger logger = Logger.getLogger(ClientConnectionHandler.class.getName());

    @Autowired
    public ClientConnectionHandler (ClientTLSProvider tlsProvider){

        this.serverSocketFactory = tlsProvider.tlsConnection();

        //Pre-Populate session Pool to reduce overhead
        this.prePopulateSessionPool();
    }

    public SSLSocket handleConnectionRequest(String host, Integer port) {
        try{

                SSLSocket sslSocket;

                sslSocket = (SSLSocket) serverSocketFactory.createSocket(host,port);
                sslSocket.startHandshake();

                SSLSession session = sslSocket.getSession();
                if (session.isValid()) {
                    this.establishSession(sslSocket);
                    logger.info("Handshake completed successfully with " + sslSocket.getInetAddress());
                } else {
                    logger.warning("Handshake failed or session is not valid with " + sslSocket.getInetAddress());
                    sslSocket.close();
                }

            return sslSocket;
        }catch (Exception e){
            return null;

        }

    }

    private void establishSession(SSLSocket connectionSocket) throws InterruptedException {

        ClientSession session = sessionPool.take();
        session.establishSocket(connectionSocket);
        sessionsMap.put(session.getSESSION_ID(),session);

        System.out.println("Sessions " + sessionsMap.size());
        for (Map.Entry<Integer, ClientSession> s: sessionsMap.entrySet()) {
            System.out.println(s.getKey());
        }

        //session.downloadThread.start();
    }

    private void prePopulateSessionPool(){
        for (int i = 0; i < ClientApplication.settings().getSessionPoolSize(); i ++){
            ClientSession session = new ClientSession();
            sessionPool.add(session);
        }
    }

}
