package Intializer;



import Connection.ClientConnectionHandler;
import Session.ClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.io.*;
import java.util.List;
import java.util.logging.Logger;

@SpringBootApplication
@RestController
@ComponentScan(basePackages = {"Connection","Authentication"})
public class Client implements CommandLineRunner {


    @Autowired
    private ClientConnectionHandler connectionHandler;
    private static Logger LOG = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
    }

    @Override
    public void run(String... args){
       // connectionHandler.handleConnectionRequest();
    }

    @GetMapping("/hello")
    public String testRouting (@RequestParam("ids") List<Integer> ids){

        return "FUNCIONOU :)";
    }
}


