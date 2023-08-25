package aicup.javaclient;

import java.util.Collections;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import aicup.javaclient.control.Initialize;

@SpringBootApplication
public class JavaclientApplication {

	public static void main(String[] args) {
		//SpringApplication.run(JavaclientApplication.class, args);
		Initialize init = Initialize.getInsIance();
		init.login();
		SpringApplication app = new SpringApplication(JavaclientApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", init.getMyPort()));
		app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
		init.ready();
    

		
		

		


	}

}
