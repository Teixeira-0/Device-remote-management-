package Intializer;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

@SpringBootApplication
public class Client implements CommandLineRunner {

    private static Logger LOG = Logger.getLogger(Client.class.getName());
    private final BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
    }

    @Override
    public void run(String... args) throws IOException, InterruptedException {

    }
}
