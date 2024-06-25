package Connection;

import Session.ClientSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/remote")
public class RemoteExecutionController {

    public RemoteExecutionController() {
    }


    @GetMapping("/execute")
    public ResponseEntity<Map<String, Object>> initializeRemoteShell(){

        ClientSession session = ClientConnectionHandler.searchSessionById(1);

        session.initializeRemoteShell();


        return new ResponseEntity<>(HttpStatus.OK);
    }
}
