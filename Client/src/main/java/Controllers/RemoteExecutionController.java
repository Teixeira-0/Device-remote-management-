package Controllers;

import Connection.ClientConnectionHandler;
import Session.ClientSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/remote")
public class RemoteExecutionController {

    public RemoteExecutionController() {
    }


    @GetMapping("/executesingle")
    public ResponseEntity<Map<String, Object>> initializeRemoteShellSingleCommand(@RequestParam("command") String command, @RequestParam("sessionid") List<Integer> sessionIds ){

        ClientSession session;

        for (Integer id: sessionIds) {
            session = ClientConnectionHandler.searchSessionById(id);
            session.setCommand(command);

            //For testing demo use this, real scenario would use threads connected to the websocket
            session.initializeRemoteShell();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/executemultiple")
    public ResponseEntity<Map<String, Object>> initializeRemoteShellMultipleCommand(@RequestParam("sessionid") int sessionId ){

        ClientSession session = ClientConnectionHandler.searchSessionById(sessionId);

        session.initializeRemoteShellMultiple();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
