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

@RestController
@RequestMapping("/data")
public class DataTransferController {

    public DataTransferController() {
    }


    @GetMapping("/download")
    public ResponseEntity<Map<String, Object>> downloadData (@RequestParam("path") String path,@RequestParam("sessionid") int id) throws IOException {

        ClientSession session = ClientConnectionHandler.searchSessionById(id);
        session.downloadData(path);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadData () throws IOException {

        ClientSession session = ClientConnectionHandler.searchSessionById(1);
        session.uploadData();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
