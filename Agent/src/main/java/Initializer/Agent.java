package Initializer;


import Connection.ConnectionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.logging.Logger;

@SpringBootApplication
@RestController
@ComponentScan(basePackages = {"Connection","Authentication","Session"})
public class Agent implements CommandLineRunner {

    @Autowired
    private ConnectionHandler connectionHandler;
    private static Logger LOG = Logger.getLogger(Agent.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(Agent.class, args);
    }

    @Override
    public void run(String... args) throws IOException, InterruptedException {
        connectionHandler.handleConnectionRequest();
    }


}
