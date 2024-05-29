package Initializer;


import Connection.ConnectionHandler;
import Settings.AppSettings;
import Settings.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.*;
import java.util.logging.Logger;

@SpringBootApplication
@ComponentScan(basePackages = "Connection")
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
