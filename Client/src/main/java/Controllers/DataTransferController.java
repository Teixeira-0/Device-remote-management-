package Controllers;

import Connection.ClientConnectionHandler;
import Session.ClientSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/data")
public class DataTransferController {

    public DataTransferController() {
    }


    @GetMapping("/download")
    public ResponseEntity<Map<String, Object>> downloadData (@RequestParam("path") String path,@RequestParam("sessionid") int id) throws IOException {

        ClientSession session = ClientConnectionHandler.searchSessionById(id);

        if(Objects.equals(session.downloadData(path), "error")){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        };

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadData (@RequestParam("path") String path, @RequestParam("sessionid") List<Integer> sessionIds){

        ClientSession session;

        for (Integer id: sessionIds) {
            session = ClientConnectionHandler.searchSessionById(id);

            if(!session.setPath(path)){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            //For testing demo use this, real scenario would use threads connected to the websocket
            session.uploadThread.start();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
