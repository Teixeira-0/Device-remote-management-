package Session;

import Authentication.ClientTLSProvider;
import Connection.ClientConnectionHandler;
import Protocol.ReadapCodesClient;
import Protocol.ReadapMessageClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import javax.net.ssl.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ClientSessionDownloadTest {

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
    public void testDownload() throws Exception {


        // Mock ReadapMessageClient
        ReadapMessageClient mockMessage = Mockito.mock(ReadapMessageClient.class);
        mockMessage.setChunk(new byte[] {12});

        Mockito.when(mockMessage.getCode()).thenReturn(ReadapCodesClient.REMOTECOMMAND);
        Mockito.when(mockMessage.getChunk()).thenReturn(new byte[] {12});

        try (MockedStatic<ReadapMessageClient> mockedStatic = Mockito.mockStatic(ReadapMessageClient.class)) {
            mockedStatic.when(() -> ReadapMessageClient.fromByteArrayRemainder(new byte[8196])).thenReturn(mockMessage);


            // Mock behavior of sessionSocket
            Mockito.when(mockSocket.getInputStream()).thenReturn(mockInputStream);
            Mockito.when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

            // Mock initial REMOTESTART message response
            Mockito.when(mockInputStream.read(ArgumentMatchers.any(byte[].class))).thenReturn(-1);

            // Test
            ClientSession remoteShell = new ClientSession();
            remoteShell.establishSocket(mockSocket);
            //remoteShell.downloadData("Fortnite.exe");
        }

        // Assertions based on mock responses
        //verify(mockOutputStream,times(2)).write(any(byte[].class)); // Verify writes
        //verify(mockInputStream,times(2)).read(any());

    }
}

class ClientSessionDownloadIntegrationTest {
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

        ReadapMessageClient response = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.DOWNLOADACK, ByteBuffer.allocate(Long.BYTES).putLong(10).array());
        out.write(response.toByteArrayRemainder());

        response = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.DOWNLOADPAYLOAD, new byte[]{10,10});
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


        Assertions.assertEquals("success",session.downloadData("Fortnite.exe"));

        setupThread.join();
    }
}
