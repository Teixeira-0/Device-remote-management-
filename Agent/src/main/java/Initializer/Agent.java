package Initializer;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

@SpringBootApplication
public class Agent implements CommandLineRunner {

    private static Logger LOG = Logger.getLogger(Agent.class.getName());
    private final BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        SpringApplication.run(Agent.class, args);
    }

    @Override
    public void run(String... args) throws IOException, InterruptedException {

    }
}
