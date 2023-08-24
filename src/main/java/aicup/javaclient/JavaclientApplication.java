package aicup.javaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import aicup.javaclient.control.Initialize;

@SpringBootApplication
public class JavaclientApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaclientApplication.class, args);
		//Initialize init = new Initialize();
		//init.login();


	}

}
