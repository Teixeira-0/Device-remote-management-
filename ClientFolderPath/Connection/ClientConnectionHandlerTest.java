package Connection;

import Authentication.ClientTLSProvider;
import Session.ClientSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientConnectionHandlerTest {

    @Mock
    private ClientTLSProvider mockClientTLSProvider;

    @Mock
    private SSLSocketFactory mockSSLSocketFactory;

    private ClientConnectionHandler connectionHandler;

    @BeforeEach
    public void setUp() {

        //Initialization of mocks
        MockitoAnnotations.openMocks(this);

        //ClientTLSProvider mock config to retorn the mock of SSLSocketFactory
        Mockito.when(mockClientTLSProvider.tlsConnection()).thenReturn(mockSSLSocketFactory);

        //Creation of connectionHandler with the inkected mock
        connectionHandler = new ClientConnectionHandler(mockClientTLSProvider);
    }


    //Unit Test to verify that the socket is being returned and not closed since the session is valid
    @Test
    void handleConnectionRequest() throws Exception {
        //Test parameters
        String host = "localhost";
        int port = 61010;

        //Configuration of mockSSLSocketFactory
        SSLSocket mockSSLSocket = Mockito.mock(SSLSocket.class);
        Mockito.when(mockSSLSocketFactory.createSocket(host, port)).thenReturn(mockSSLSocket);

        //Configuration of mock SSLSession
        SSLSession mockSession = Mockito.mock(SSLSession.class);
        Mockito.when(mockSSLSocket.getSession()).thenReturn(mockSession);

        // Configure mockSession to return true when isValid() is called
        Mockito.when(mockSession.isValid()).thenReturn(true);

        //Test method call
        String message = connectionHandler.handleConnectionRequest(host, port);


        //Verification that the method was correctly called
        Mockito.verify(mockSSLSocketFactory).createSocket(host, port);
        Mockito.verify(mockSSLSocket).getSession();
        Mockito.verify(mockSession).isValid();

        //Verifications of the created socket
        Assertions.assertNotNull(message, "Socket should not be null");
        Assertions.assertEquals(message,"success");

    }

    //Unit Test to verify that the socket is being returned and closed since the session is valid
    @Test
    void handleConnectionRequestFail() throws Exception {
        //Test parameters
        String host = "localhost";
        int port = 61010;

        //Configuration of mockSSLSocketFactory
        SSLSocket mockSSLSocket = Mockito.mock(SSLSocket.class);
        Mockito.when(mockSSLSocketFactory.createSocket(host, port)).thenReturn(mockSSLSocket);

        //Configuration of mock SSLSession
        SSLSession mockSession = Mockito.mock(SSLSession.class);
        Mockito.when(mockSSLSocket.getSession()).thenReturn(mockSession);

        // Configure mockSession to return true when isValid() is called
        Mockito.when(mockSession.isValid()).thenReturn(false);

        //Test method call
        String createdSocket = connectionHandler.handleConnectionRequest(host, port);


        //Verification that the method was correctly called
        Mockito.verify(mockSSLSocketFactory).createSocket(host, port);
        Mockito.verify(mockSSLSocket).getSession();
        Mockito.verify(mockSession).isValid();

        //Verifications of the created socket
        Assertions.assertNotNull(createdSocket, "Socket should not be null");
        Assertions.assertEquals(createdSocket, "certificate");

    }

    //Unit Test to verify that the created session is inserted and found on the hashmap
    @Test
    public void establishSession() throws Exception {
        //Test parameters
        String host = "localhost";
        int port = 61010;

        //Configuration of mockSSLSocketFactory
        SSLSocket mockSSLSocket = Mockito.mock(SSLSocket.class);
        Mockito.when(mockSSLSocketFactory.createSocket(host, port)).thenReturn(mockSSLSocket);

        //Configuration of mock SSLSession
        SSLSession mockSession = Mockito.mock(SSLSession.class);
        Mockito.when(mockSSLSocket.getSession()).thenReturn(mockSession);

        // Configure mockSession to return true when isValid() is called
        Mockito.when(mockSession.isValid()).thenReturn(true);

        //Method call
        connectionHandler.handleConnectionRequest(host, port);


        //Verification that the method was correctly called
        Mockito.verify(mockSSLSocketFactory).createSocket(host, port);
        Mockito.verify(mockSSLSocket).getSession();
        Mockito.verify(mockSession).isValid();

        ClientSession session = ClientConnectionHandler.searchSessionById(1);

        Assertions.assertNotNull(session, "The Session must be found on the hashmap");

    }
}