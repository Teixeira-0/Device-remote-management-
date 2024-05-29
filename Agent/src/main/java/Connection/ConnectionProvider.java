package Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;

@Component
public class ConnectionProvider {

    private final ServerSocket agentSocket;

    private static final Logger logger = LoggerFactory.getLogger(ConnectionProvider.class);

    public ConnectionProvider () throws IOException {

        this.agentSocket = new ServerSocket(61010);
        logger.info("Open port 61010 for connection");
    }

    public ServerSocket getServerSocket(){
        return this.agentSocket;
    }

}
