package Controllers;

import Authentication.ClientTLSProvider;
import Connection.ClientConnectionHandler;
import Intializer.Client;
import Session.ClientSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import javax.net.ssl.SSLSocket;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = Client.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConnectionControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ClientConnectionHandler connectionHandler;

    @Test
    void testSuccessFullConection() {

        // Mock the behavior of the connection handler to return the mock socket
        when(connectionHandler.handleConnectionRequest("localhost", 61010)).thenReturn("success");

        // Act: Send HTTP GET request to /connection/create?host=localhost&port=61010
        ResponseEntity<Void> response = restTemplate.getForEntity("/connection/create?host=localhost&port=61010", Void.class);

        // Assert: Verify the response status is OK (200)
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void testCertificateErrorConection() {
        // Arrange: Create a mock SSLSocket
        SSLSocket mockSocket = mock(SSLSocket.class);

        // Mock the behavior of the connection handler to return the mock socket
        when(connectionHandler.handleConnectionRequest("localhost", 61010)).thenReturn("certificate");

        // Act: Send HTTP GET request to /connection/create?host=localhost&port=61010
        ResponseEntity<Void> response = restTemplate.getForEntity("/connection/create?host=localhost&port=61010", Void.class);

        // Assert: Verify the response status is OK (200)
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testHostErrorConection() {
        // Arrange: Create a mock SSLSocket
        SSLSocket mockSocket = mock(SSLSocket.class);

        // Mock the behavior of the connection handler to return the mock socket
        when(connectionHandler.handleConnectionRequest("localhost", 61010)).thenReturn("host");

        // Act: Send HTTP GET request to /connection/create?host=localhost&port=61010
        ResponseEntity<Void> response = restTemplate.getForEntity("/connection/create?host=localhost&port=61010", Void.class);

        // Assert: Verify the response status is OK (200)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPortErrorConection() {
        // Arrange: Create a mock SSLSocket
        SSLSocket mockSocket = mock(SSLSocket.class);

        // Mock the behavior of the connection handler to return the mock socket
        when(connectionHandler.handleConnectionRequest("localhost", 61010)).thenReturn("port");

        // Act: Send HTTP GET request to /connection/create?host=localhost&port=61010
        ResponseEntity<Void> response = restTemplate.getForEntity("/connection/create?host=localhost&port=61010", Void.class);

        // Assert: Verify the response status is OK (200)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }



}