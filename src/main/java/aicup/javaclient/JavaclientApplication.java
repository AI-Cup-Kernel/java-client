package aicup.javaclient;

import java.util.Collections;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import aicup.javaclient.control.Initialize;

@SpringBootApplication
public class JavaclientApplication {


	// Main Method
	public static void main(String[] args) {
		

		// login into the kernal server
		Initialize init = Initialize.getInstance();
		init.login();


		// launching the Client server based on the kernal server port
		SpringApplication app = new SpringApplication(JavaclientApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", init.getMyPort()));
		app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

		// Sending an acknowledgement to the server that the client server is up successfully
		init.ready();		


	}

}
