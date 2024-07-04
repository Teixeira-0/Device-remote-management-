package Session;

import Authentication.TLSProvider;
import Protocol.ReadapCodes;
import Protocol.ReadapMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;

public class StatusTest {

    ReadapMessage sent;
    void setup() throws NoSuchAlgorithmException, IOException, KeyManagementException, CertificateException, UnrecoverableKeyException, KeyStoreException, InterruptedException {

        // Load the KeyStore
        KeyStore keyStore = KeyStore.getInstance("JKS");
        FileInputStream keyStoreFile = new FileInputStream("/Users/felix/Documents/3 Ano/PESTI/Device-remote-management-/TLS/client-keystore.jks");
        char[] keyStorePassword = "Password1".toCharArray();
        keyStore.load(keyStoreFile, keyStorePassword);

        // Create KeyManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keyStorePassword);

        // Load the TrustStore
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        FileInputStream trustStoreFile = new FileInputStream("/Users/felix/Documents/3 Ano/PESTI/Device-remote-management-/TLS/client-truststore.p12");
        char[] trustStorePassword = "Password1".toCharArray();
        trustStore.load(trustStoreFile, trustStorePassword);

        // Create TrustManagerFactory
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);

        // Create SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        // Create SSLServerSocketFactory
        SSLSocketFactory sslServerSocketFactory = sslContext.getSocketFactory();

        SSLSocket sslSocket;
        Thread.sleep(1000);
        sslSocket = (SSLSocket) sslServerSocketFactory.createSocket("localhost",61010);

        InputStream in = sslSocket.getInputStream();
        OutputStream out = sslSocket.getOutputStream();


        byte[] chunk = new byte[8196];
        in.read(chunk);
        sent = ReadapMessage.fromByteArrayRemainder(chunk);

        in.read(chunk);
        sent = ReadapMessage.fromByteArrayRemainder(chunk);

        in.read(chunk);
        sent = ReadapMessage.fromByteArrayRemainder(chunk);



    }

    @Test
    void integrationTest() throws IOException, InterruptedException {

        Thread setupThread = new Thread(() -> {
            try {
                setup();
            } catch (NoSuchAlgorithmException | IOException | KeyManagementException | CertificateException |
                     UnrecoverableKeyException | KeyStoreException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        TLSProvider tls = new TLSProvider();


        SSLServerSocket sslSocketFactory = tls.tlsConnection();
        setupThread.start();
        SSLSocket socket = (SSLSocket) sslSocketFactory.accept();


        Session session = new Session();
        session.establishSocket(socket);

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        session.statusGathering(in,out);

        Assertions.assertFalse(new String(sent.getChunk(), 0, sent.getChunkLength(), StandardCharsets.UTF_8).isBlank());


    }
}
