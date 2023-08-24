package aicup.javaclient;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import aicup.javaclient.control.Initialize;

@SpringBootApplication
public class JavaclientApplication {

	public static void main(String[] args) {
		//SpringApplication.run(JavaclientApplication.class, args);
		Initialize init = new Initialize();
		init.login();
		SpringApplication app = new SpringApplication(JavaclientApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", init.getMyPort()));
        app.run(args);
    

		
		

		


	}

}
