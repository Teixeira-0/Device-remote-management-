package Authentication;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

public class TLS {
    public TLS() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {

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
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(8443);
        sslServerSocket.setNeedClientAuth(true); // Require client authentication


        System.out.println("SSL Server started on port 8443");
        Logger logger = Logger.getLogger(TLS.class.getName());
        while (true) {
            SSLSocket sslSocket = null;

            try {
                sslSocket = (SSLSocket) sslServerSocket.accept();
                sslSocket.startHandshake();

                SSLSession session = sslSocket.getSession();
                if (session.isValid()) {
                    logger.info("Handshake completed successfully with " + sslSocket.getInetAddress());

                    InputStream inputStream = sslSocket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead = inputStream.read(buffer);
                    System.out.println("Received: " + new String(buffer, 0, bytesRead));
                    // Continue handling the connection
                } else {
                    logger.warning("Handshake failed or session is not valid with " + sslSocket.getInetAddress());
                    sslSocket.close();
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

}
