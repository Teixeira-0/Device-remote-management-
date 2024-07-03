package Session;

import Authentication.ClientTLSProvider;
import Connection.ClientConnectionHandler;
import Protocol.ReadapCodesClient;
import Protocol.ReadapMessageClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
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
    public void testDownload() throws Exception {


        // Mock ReadapMessageClient
        ReadapMessageClient mockMessage = mock(ReadapMessageClient.class);
        mockMessage.setChunk(new byte[] {12});

        when(mockMessage.getCode()).thenReturn(ReadapCodesClient.REMOTECOMMAND);
        when(mockMessage.getChunk()).thenReturn(new byte[] {12});

        try (MockedStatic<ReadapMessageClient> mockedStatic = mockStatic(ReadapMessageClient.class)) {
            mockedStatic.when(() -> ReadapMessageClient.fromByteArrayRemainder(new byte[8196])).thenReturn(mockMessage);


            // Mock behavior of sessionSocket
            when(mockSocket.getInputStream()).thenReturn(mockInputStream);
            when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

            // Mock initial REMOTESTART message response
            when(mockInputStream.read(any(byte[].class))).thenReturn(-1);

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


        assertEquals("success",session.downloadData("Fortnite.exe"));

    }
}
