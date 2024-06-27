package Connection;

import Authentication.TLSProvider;
import Session.Session;
import Settings.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

@Component
@RestController
public class ConnectionHandler {

    //private final ServerSocket agentSocket;
    private final SSLServerSocket serverSocket;
    private final LinkedBlockingQueue<Session> sessionPool = new LinkedBlockingQueue<Session>();
    private ExecutorService threadPool;
    private final Logger logger = Logger.getLogger(ConnectionHandler.class.getName());

    @Autowired
    public ConnectionHandler(TLSProvider tlsProvider){
        //this.agentSocket = connectionProvider.getServerSocket();
        this.serverSocket = tlsProvider.tlsConnection();

        // The number of threads must be equal or superior to the number of sessions in the pool
        this.threadPool = Executors.newFixedThreadPool(Application.settings().getSessionPoolSize());

        //Pre-Populate session Pool to reduce overhead
        this.prePopulateSessionPool();
    }

    public void handleConnectionRequest() {

        try{
            while(true){
                SSLSocket sslSocket;

                sslSocket = (SSLSocket) serverSocket.accept();
                sslSocket.startHandshake();
                /*
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) threadPool;
                System.out.println(threadPoolExecutor.getPoolSize());




                 */
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
