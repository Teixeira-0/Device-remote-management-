package Controllers;



import Authentication.ClientTLSProvider;
import Connection.ClientConnectionHandler;
import Connection.ClientConnectionHandlerTest;
import Intializer.Client;
import Session.ClientSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Client.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatusControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;


    @Mock
    private ClientTLSProvider mockClientTLSProvider;

    @Mock
    private SSLSocketFactory mockSSLSocketFactory;

    private ClientConnectionHandler connectionHandler;

    @BeforeEach
    public void setUp() throws IOException {

        //Initialization of mocks
        MockitoAnnotations.openMocks(this);

        //ClientTLSProvider mock config to retorn the mock of SSLSocketFactory
        when(mockClientTLSProvider.tlsConnection()).thenReturn(mockSSLSocketFactory);

        //Creation of connectionHandler with the inkected mock
        connectionHandler = new ClientConnectionHandler(mockClientTLSProvider);

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

    @Test
    public void testDownloadData_Success() {

        ClientSession mockSession;

        try (MockedStatic<ClientConnectionHandler> mockedStatic = mockStatic(ClientConnectionHandler.class)) {

            mockSession = mock(ClientSession.class);


            mockedStatic.when(() -> ClientConnectionHandler.searchSessionById(1)).thenReturn(mockSession);

            // Mock statusGathering method
            Mockito.doNothing().when(mockSession).statusGathering();

            // Send HTTP GET request to /status/cpu?sessionid=1
            ResponseEntity<Void> response = restTemplate.getForEntity("/status/cpu?sessionid=1", Void.class);


            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

}
