package Session;

import Authentication.ClientTLSProvider;
import Protocol.ReadapCodesClient;
import Protocol.ReadapMessageClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class ClientSessionStatustest {

    private SSLSocket mockSocket;
    private InputStream mockInputStream;
    private OutputStream mockOutputStream;
    private ClientSession session;  // Replace with the actual class name
    private ByteArrayOutputStream byteArrayOutputStream;

    @BeforeEach
    public void setUp() throws Exception {

        // Mock sessionSocket and its streams
        mockSocket = Mockito.mock(SSLSocket.class);
        mockInputStream = Mockito.mock(InputStream.class);
        mockOutputStream = Mockito.mock(OutputStream.class);

        byteArrayOutputStream = new ByteArrayOutputStream();
        mockOutputStream = Mockito.spy(byteArrayOutputStream);

        Mockito.when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        session = new ClientSession();
        session.establishSocket(mockSocket);


    }


    @Test
    public void testStatusGathering() throws Exception {
        // Mock ReadapMessageClient
        ReadapMessageClient mockMessage = Mockito.mock(ReadapMessageClient.class);
        Mockito.when(mockMessage.getCode()).thenReturn(ReadapCodesClient.REMOTECOMMAND);
        Mockito.when(mockMessage.getChunk()).thenReturn("Mock response data".getBytes());

        // Mock behavior of sessionSocket
        Mockito.when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        // Mock initial REMOTESTART message response
        Mockito.when(mockInputStream.read(ArgumentMatchers.any(byte[].class))).thenReturn(-1);

        // Test your method
        ClientSession session = new ClientSession();
        session.establishSocket(mockSocket);
        session.statusGathering();

        // Assertions based on mock responses
        Mockito.verify(mockOutputStream, Mockito.times(1)).write(ArgumentMatchers.any(byte[].class)); // Verify writes
        Mockito.verify(mockInputStream, Mockito.times(2)).read(ArgumentMatchers.any());

    }

    @Test
    public void testMessageGeneration() throws Exception {

        ReadapMessageClient expectedMessage =  new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.STATUS,new byte[0]);


        session.statusGathering();

        Mockito.verify(mockOutputStream).write(expectedMessage.toByteArrayRemainder());


    }

}


class ClientSessionStatusGatheringIntegrationTest {

    void setup() throws NoSuchAlgorithmException, IOException, KeyManagementException, CertificateException, UnrecoverableKeyException, KeyStoreException {

        KeyStore keyStore = KeyStore.getInstance("JKS");
        FileInputStream keyStoreFile = new FileInputStream("src/test/java/certificates/server-keystore.jks");
        char[] keyStorePassword = "Password1".toCharArray();
        keyStore.load(keyStoreFile, keyStorePassword);

        // Create KeyManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keyStorePassword);

        // Load the TrustStore
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        FileInputStream trustStoreFile = new FileInputStream("src/test/java/certificates/server-truststore.p12");
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

        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(61010);
        sslServerSocket.setWantClientAuth(false);
        sslServerSocket.setNeedClientAuth(false);
        SSLSocket socket = (SSLSocket) sslServerSocket.accept();



        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        ReadapMessageClient response = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.STATUSACK,  new byte[0]);
        out.write(response.toByteArrayRemainder());

        response = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.STATUSDATA,"success output".getBytes());
        out.write(response.toByteArrayRemainder());

        sslServerSocket.close();
        Thread.currentThread().interrupt();
    }


    @Test
    void integrationTest() throws IOException, InterruptedException {

        Thread setupThread = new Thread(() -> {
            try {
                setup();
            } catch (NoSuchAlgorithmException | IOException | KeyManagementException | CertificateException |
                     UnrecoverableKeyException | KeyStoreException e) {
                e.printStackTrace();
            }
        });

        setupThread.start();

        ClientTLSProvider tls = new ClientTLSProvider();


        SSLSocketFactory sslSocketFactory = tls.tlsConnection();
        SSLSocket socket1 = (SSLSocket) sslSocketFactory.createSocket("localhost",61010);


        ClientSession session = new ClientSession();
        session.establishSocket(socket1);

        session.statusGathering();

        setupThread.join();

    }


}
