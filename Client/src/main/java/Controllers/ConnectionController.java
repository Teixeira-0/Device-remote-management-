package Controllers;


import Connection.ClientConnectionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLSocket;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/connection")
public class ConnectionController {

    @Autowired
    private ClientConnectionHandler connectionHandler;

    public ConnectionController() {
    }

    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> createConnection (@RequestParam("host") String host, @RequestParam("port") Integer port ){

        SSLSocket socket = connectionHandler.handleConnectionRequest(host,port);



        if(socket == null){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Constructing the response map
        Map<String, Object> response = new HashMap<>();
        response.put("host", host);
        response.put("port", port);
        response.put("Success", true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
