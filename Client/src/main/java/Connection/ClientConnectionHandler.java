package Connection;

import Authentication.ClientTLSProvider;
import Session.ClientSession;
import Settings.ClientApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;


@Component
public class ClientConnectionHandler {

    //private final ServerSocket agentSocket;
    private final SSLSocketFactory serverSocketFactory;

    private static final HashMap<Integer, ClientSession> sessionsMap = new HashMap<Integer, ClientSession>();

    private final Logger logger = Logger.getLogger(ClientConnectionHandler.class.getName());

    @Autowired
    public ClientConnectionHandler (ClientTLSProvider tlsProvider){

        this.serverSocketFactory = tlsProvider.tlsConnection();
    }

    public String handleConnectionRequest(String host, Integer port) {
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
                    return "certificate";
                }


            return "success";
        }
        catch (ConnectException connectException){
            return "host";
        }catch (IllegalArgumentException e){
            return "port";
        }catch (IOException e){
            return null;
        }

    }

    private void establishSession(SSLSocket connectionSocket) {

        ClientSession session = new ClientSession();
        session.establishSocket(connectionSocket);
        sessionsMap.put(session.getSESSION_ID(),session);

        /*
        System.out.println("Sessions " + sessionsMap.size());
        for (Map.Entry<Integer, ClientSession> s: sessionsMap.entrySet()) {
            System.out.println(s.getKey());
        }
         */

        //session.downloadThread.start();
    }


    public static ClientSession searchSessionById(Integer sessionId) {
        return sessionsMap.get(sessionId);
    }
}
