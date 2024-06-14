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

        try {
            // Load the KeyStore
            KeyStore keyStore = KeyStore.getInstance("JKS");
            FileInputStream keyStoreFile = new FileInputStream("/Users/felix/Documents/3 Ano/PESTI/Device-remote-management-/TLS/server-keystore.jks");
            char[] keyStorePassword = "Password1".toCharArray();
            keyStore.load(keyStoreFile, keyStorePassword);

            // Create KeyManagerFactory
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keyStorePassword);

            // Load the TrustStore
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            FileInputStream trustStoreFile = new FileInputStream("/Users/felix/Documents/3 Ano/PESTI/Device-remote-management-/TLS/server-truststore.p12");
            char[] trustStorePassword = "Password1".toCharArray();
            trustStore.load(trustStoreFile, trustStorePassword);

            // Create TrustManagerFactory
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);

            // Create SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // Create SSLServerSocketFactory
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(Application.settings().getConnectionPort());
            sslServerSocket.setNeedClientAuth(true); // Require client authentication


            System.out.println("SSL Server started on port 8443");
            Logger logger = Logger.getLogger(TLSProvider.class.getName());
            logger.log(new LogRecord(Level.INFO, "SSL Server started on port" + Application.settings().getConnectionPort()));

            return sslServerSocket;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}
