package Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/connection")
public class ConnectionController {


    public ConnectionController() {
    }

    @GetMapping("/create")
    public String testRouting (@RequestParam("ids") List<Integer> ids){

        return "FUNCIONOU :)";
    }
}
