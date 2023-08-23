package aicup.javaclient.control;


import java.io.FileReader;
import java.security.SecureRandom;
import java.util.Random;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Initialize {


    
    private int password; // the password that I should recieve from the server to authenticate the server 
    private int token; // the token that I should send to the server in my requests for authentication
    private String serverIp; // the ip of the kernal server
    private int serverPort;  // the port of the kernal server
    private int myPort; // the port that I should run my api on
    
    

    // a contructor to initialize the needed variables
    public Initialize(){

        // TODO: disabling the proxy
        
        // init the password
        Random rand = new SecureRandom(); 
        password = rand.nextInt(1000000000, 999999999);

        // init the server configuration
        JSONParser parser = new JSONParser();
        try {
           Object obj = parser.parse(new FileReader("src\\main\\java\\aicup\\javaclient\\config.json"));
           JSONObject jsonObject = (JSONObject)obj;
           serverIp = (String)jsonObject.get("server_ip");
           serverPort  = (int)jsonObject.get("server_port");
           
           
        } catch(Exception e) {
            System.out.println("Error in reading conf.json (the path of the file should be in src\\main\\java\\aicup\\javaclient\\config.json)");
        }


    }


 




}
