package Controllers;

import Connection.ClientConnectionHandler;
import Session.ClientSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
@RequestMapping("/status")
public class StatusController {

    public StatusController (){

    }


    @GetMapping("/cpu")
    public ResponseEntity<Map<String, Object>> statusGathering(@RequestParam("sessionid") int id) {

        ClientSession session = ClientConnectionHandler.searchSessionById(id);

        session.statusGathering();

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
