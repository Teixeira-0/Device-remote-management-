package READAP;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;

@SpringBootApplication
public class ReadapApplication implements CommandLineRunner{

	private static Logger LOG = Logger.getLogger(ReadapApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(ReadapApplication.class, args);
	}

	@Override
	public void run(String... args){

		LOG.info("#Running....");


	}

}
