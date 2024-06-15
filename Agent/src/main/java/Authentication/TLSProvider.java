package Authentication;

import Settings.Application;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

@Component
public class TLSProvider {
    public SSLServerSocket tlsConnection() {

        String keystoreType = Application.settings().getKeystoreType();
        String keystorePath = Application.settings().getKeystorePath();
        String managerAlgorithm = Application.settings().getManagersAlgorithm();
        String truststoreType = Application.settings().getTruststoreType();
        String trustStorePath = Application.settings().getTruststorePath();
        String contextProtocol = Application.settings().getContextProtocol();

        try {
            // Load the KeyStore
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            FileInputStream keyStoreFile = new FileInputStream(keystorePath);
            char[] keyStorePassword = "Password1".toCharArray();
            keyStore.load(keyStoreFile, keyStorePassword);

            // Create KeyManagerFactory
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(managerAlgorithm);
            keyManagerFactory.init(keyStore, keyStorePassword);

            // Load the TrustStore
            KeyStore trustStore = KeyStore.getInstance(truststoreType);
            FileInputStream trustStoreFile = new FileInputStream(trustStorePath);
            char[] trustStorePassword = "Password1".toCharArray();
            trustStore.load(trustStoreFile, trustStorePassword);

            // Create TrustManagerFactory
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(managerAlgorithm);
            trustManagerFactory.init(trustStore);

            // Create SSLContext
            SSLContext sslContext = SSLContext.getInstance(contextProtocol);
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // Create SSLServerSocketFactory
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(Application.settings().getConnectionPort());
            sslServerSocket.setNeedClientAuth(true); // Require client authentication


            Logger logger = Logger.getLogger(TLSProvider.class.getName());
            logger.log(new LogRecord(Level.INFO, "SSL Server started on port " + Application.settings().getConnectionPort()));

            return sslServerSocket;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}
