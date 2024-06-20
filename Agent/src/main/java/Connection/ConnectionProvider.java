package Connection;

import Settings.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;

//#################################DEPRECATED#################################

public class ConnectionProvider {

    private final ServerSocket agentSocket;

    private static final Logger logger = LoggerFactory.getLogger(ConnectionProvider.class);

    public ConnectionProvider () throws IOException {

        this.agentSocket = new ServerSocket(Application.settings().getConnectionPort());
        logger.info("Open port " + Application.settings().getConnectionPort() +" for connection");
    }

    public ServerSocket getServerSocket(){
        return this.agentSocket;
    }

}


