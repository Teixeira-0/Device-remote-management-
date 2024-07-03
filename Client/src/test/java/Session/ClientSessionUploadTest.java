package Session;

import Authentication.ClientTLSProvider;
import Protocol.ReadapCodesClient;
import Protocol.ReadapMessageClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class ClientSessionUploadTest {

    private SSLSocket mockSocket;
    private InputStream mockInputStream;
    private OutputStream mockOutputStream;
    private ClientSession session;  // Replace with the actual class name
    private ByteArrayOutputStream byteArrayOutputStream;

    @BeforeEach
    public void setUp() throws Exception {

        // Mock sessionSocket and its streams
        mockSocket = mock(SSLSocket.class);
        mockInputStream = mock(InputStream.class);
        mockOutputStream = mock(OutputStream.class);

        byteArrayOutputStream = new ByteArrayOutputStream();
        mockOutputStream = spy(byteArrayOutputStream);

        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        session = new ClientSession();
        session.establishSocket(mockSocket);
    }

    @Test
    public void testuploadErrorExecution() throws Exception {

        // Mock ReadapMessageClient
        ReadapMessageClient mockMessage = mock(ReadapMessageClient.class);
        when(mockMessage.getCode()).thenReturn(ReadapCodesClient.REMOTECOMMAND);
        when(mockMessage.getChunk()).thenReturn("Mock response data".getBytes());

        // Mock behavior of sessionSocket
        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        // Mock initial REMOTESTART message response
        when(mockInputStream.read(any(byte[].class))).thenReturn(-1);

        // Test
        ClientSession remoteShell = new ClientSession();
        remoteShell.establishSocket(mockSocket);
        remoteShell.uploadData("Billy.png");


        // Assertions based on mock responses
        verify(mockOutputStream,times(2)).write(any(byte[].class)); // Verify writes
        verify(mockInputStream,times(1)).read(any());

    }

    @Test
    public void testuploadExecution() throws Exception {

        // Mock ReadapMessageClient
        ReadapMessageClient mockMessage = mock(ReadapMessageClient.class);
        when(mockMessage.getCode()).thenReturn(ReadapCodesClient.REMOTECOMMAND);
        when(mockMessage.getChunk()).thenReturn("Mock response data".getBytes());

        // Mock behavior of sessionSocket
        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        // Mock initial REMOTESTART message response
        when(mockInputStream.read(any(byte[].class))).thenReturn(-1);

        // Test
        ClientSession remoteShell = new ClientSession();
        remoteShell.establishSocket(mockSocket);
        remoteShell.uploadData("src/main/java/Protocol/ReadapCodesClient.java");


        // Assertions based on mock responses
        verify(mockOutputStream,times(3)).write(any(byte[].class)); // Verify writes
        verify(mockInputStream,times(0)).read(any());

    }
}

class ClientSessioUploadIntegrationTest {

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

        ReadapMessageClient response = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.UPLOADACK,  new byte[0]);;
        out.write(response.toByteArrayRemainder());

        response = new ReadapMessageClient(ReadapCodesClient.VERSION, ReadapCodesClient.UPLOADACK, new byte[0]);
        out.write(response.toByteArrayRemainder());
    }

    @Test
    void integrationTest() throws IOException {

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

        session.uploadData("src/main/java/Intializer/Client.java");



    }
}
