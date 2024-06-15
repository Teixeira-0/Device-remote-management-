package Intializer;



import Connection.ClientConnectionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


import java.io.*;
import java.util.logging.Logger;

@SpringBootApplication
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
        connectionHandler.handleConnectionRequest();
    }
}
