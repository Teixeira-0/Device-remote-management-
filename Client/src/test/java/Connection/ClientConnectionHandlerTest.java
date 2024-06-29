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

class ClientConnectionHandlerTest {

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
        when(mockClientTLSProvider.tlsConnection()).thenReturn(mockSSLSocketFactory);

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
        SSLSocket mockSSLSocket = mock(SSLSocket.class);
        when(mockSSLSocketFactory.createSocket(host, port)).thenReturn(mockSSLSocket);

        //Configuration of mock SSLSession
        SSLSession mockSession = mock(SSLSession.class);
        when(mockSSLSocket.getSession()).thenReturn(mockSession);

        // Configure mockSession to return true when isValid() is called
        when(mockSession.isValid()).thenReturn(true);

        //Test method call
        SSLSocket createdSocket = connectionHandler.handleConnectionRequest(host, port);


        //Verification that the method was correctly called
        verify(mockSSLSocketFactory).createSocket(host, port);
        verify(mockSSLSocket).getSession();
        verify(mockSession).isValid();

        //Verifications of the created socket
        assertNotNull(createdSocket, "Socket should not be null");
        assertFalse(createdSocket.isClosed(), "Socket should be connected");

    }

    //Unit Test to verify that the socket is being returned and closed since the session is valid
    @Test
    void handleConnectionRequestFail() throws Exception {
        //Test parameters
        String host = "localhost";
        int port = 61010;

        //Configuration of mockSSLSocketFactory
        SSLSocket mockSSLSocket = mock(SSLSocket.class);
        when(mockSSLSocketFactory.createSocket(host, port)).thenReturn(mockSSLSocket);

        //Configuration of mock SSLSession
        SSLSession mockSession = mock(SSLSession.class);
        when(mockSSLSocket.getSession()).thenReturn(mockSession);

        // Configure mockSession to return true when isValid() is called
        when(mockSession.isValid()).thenReturn(false);

        //Test method call
        SSLSocket createdSocket = connectionHandler.handleConnectionRequest(host, port);


        //Verification that the method was correctly called
        verify(mockSSLSocketFactory).createSocket(host, port);
        verify(mockSSLSocket).getSession();
        verify(mockSession).isValid();

        //Verifications of the created socket
        assertNotNull(createdSocket, "Socket should not be null");
        assertFalse(createdSocket.isClosed(), "Socket should be connected");

    }

    //Unit Test to verify that the created session is inserted and found on the hashmap
    @Test
    void establishSession() throws Exception {
        //Test parameters
        String host = "localhost";
        int port = 61010;

        //Configuration of mockSSLSocketFactory
        SSLSocket mockSSLSocket = mock(SSLSocket.class);
        when(mockSSLSocketFactory.createSocket(host, port)).thenReturn(mockSSLSocket);

        //Configuration of mock SSLSession
        SSLSession mockSession = mock(SSLSession.class);
        when(mockSSLSocket.getSession()).thenReturn(mockSession);

        // Configure mockSession to return true when isValid() is called
        when(mockSession.isValid()).thenReturn(true);

        //Method call
        connectionHandler.handleConnectionRequest(host, port);


        //Verification that the method was correctly called
        verify(mockSSLSocketFactory).createSocket(host, port);
        verify(mockSSLSocket).getSession();
        verify(mockSession).isValid();

        ClientSession session = ClientConnectionHandler.searchSessionById(1);

        assertNotNull(session, "The Session must be found on the hashmap");

    }
}