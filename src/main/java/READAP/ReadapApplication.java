package main.java.READAP;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

class Shared{
	volatile long startTime= 0;
}

@SpringBootApplication
public class ReadapApplication implements CommandLineRunner {

	private static Logger LOG = Logger.getLogger(ReadapApplication.class.getName());
	private final BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();

	public static void main(String[] args) {
		SpringApplication.run(ReadapApplication.class, args);
	}

	@Override
	public void run(String... args) throws IOException, InterruptedException {




	}
}
