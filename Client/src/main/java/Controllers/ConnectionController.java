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
import java.util.Objects;

@RestController
@RequestMapping("/connection")
public class ConnectionController {

    @Autowired
    private ClientConnectionHandler connectionHandler;

    public ConnectionController() {
    }

    @GetMapping("/create")
    public ResponseEntity<Map<String, Object>> createConnection (@RequestParam("host") String host, @RequestParam("port") Integer port ){

        String message = connectionHandler.handleConnectionRequest(host,port);

        if(!Objects.equals(message, "success") && !Objects.equals(message, "certificate")){
            // Constructing the response map
            Map<String, Object> response = new HashMap<>();
            response.put("Error","Invalid" + message);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }else if(Objects.equals(message, "certificate")){
            // Constructing the response map
            Map<String, Object> response = new HashMap<>();
            response.put("Error","Invalid" + message);
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }

        // Constructing the response map
        Map<String, Object> response = new HashMap<>();
        response.put("host", host);
        response.put("port", port);
        response.put("Success", true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
