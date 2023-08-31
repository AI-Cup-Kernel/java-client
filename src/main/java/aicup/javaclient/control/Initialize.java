package aicup.javaclient.control;


import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import org.json.simple.*;
import org.json.simple.parser.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;



/**
 * 
 * A class responsible for initializing the connection between the client server and the kernel server.
 * 
*/

@Service
public class Initialize {


    
    private int password; // the password that I should recieve from the server to authenticate the server 
    private String token; // the token that I should send to the server in my requests for authentication
    private String serverIp; // the ip of the kernel server
    private int serverPort;  // the port of the Kernel server
    private int myPort; // the port that I should run my server on    
    private int id; // the player id
    private final RestTemplate restTemplate; // an object that manage the http requests that I send
    private String url; //The complete server URL.


    private static Initialize initialize;

    // A contructor to initialize the needed variables
    private Initialize(){
    
        // Initialize the password
        Random rand = new SecureRandom(); 
        password = rand.nextInt(100000000, 999999999);

        // Initialize the Kernel server configuration
        JSONParser parser = new JSONParser();
        try {
            // Reading the Kernel server Information from the config file
            InputStream configFile = Initialize.class.getResourceAsStream("config.json");
            if (configFile != null) {
                Object obj = parser.parse(new InputStreamReader(configFile));
                JSONObject jsonObject = (JSONObject) obj;
                String serverIp = (String) jsonObject.get("server_ip");
                int serverPort = ((Long) jsonObject.get("server_port")).intValue();
                
                // Formatting the URL
                url = "http://" + serverIp + ":" + serverPort;   
            } else {
                System.out.println("config.json not found.");
                System.exit(1);    
            }
        }
        // Handling any error that may occur when reading the config file
        catch(Exception e) {
            //System.out.println("Error in reading conf.json (the path of the file should be in src\\main\\java\\aicup\\javaclient\\config.json)");
            e.printStackTrace();
            System.exit(1);
        }
        
        // Initializing the RestTemplate object to send HTTP requests using it.
        this.restTemplate = new RestTemplate();
    }

    // Implementing the Singleton design pattern for our Initialize class to ensure a consistent object across multiple classes
    public static Initialize getInstance(){
        if(Objects.isNull(initialize)){
            initialize = new Initialize();
        }
        return initialize;
    }

    

    /**
     * 
     * A function that sends an HTTP request to the Kernel server for the purpose of logging into the game.
     * 
     * It sends the password variable in the form data format, allowing the kernel server to include it in the
     * x-access-token header each time its made request. This enables the client server to authenticate 
     * the Kernel server request.
     * 
     * it receives the player ID and the token, which the client needs to include in every request for authentication,
     * along with the port on which the client server should operate.
     * 
     */
    
    public void login(){

        // Constructing the url
        String path = url + "/login";
        
        // Create form data using MultiValueMap
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("token", Integer.toString(password));
        // Request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
        
        // Sending the request
        try{
            ResponseEntity<String> response = restTemplate.postForEntity(path, requestEntity, String.class);
        
            // Reading the response
            JSONParser parser = new JSONParser();
            try {
                JSONObject jsonResponse = (JSONObject) parser.parse(response.getBody());
                try{
                    token = (String)jsonResponse.get("token");
                    id = ((Long) jsonResponse.get("player_id")).intValue();
                    myPort = ((Long) jsonResponse.get("port")).intValue();;
                }catch (Exception e){
                    System.out.println("there is a problem in the server response");
                    System.out.println((String) jsonResponse.get("error"));
                    System.exit(0);
                }
            } catch (ParseException e) {
                System.out.println("there is a problem in the server response");
                System.out.println("there is no error message in the response");
                System.exit(0);
            }
        }catch (ResourceAccessException e){
            System.out.println("Connection Refused or Unavailable: " + e.getMessage());
            System.exit(0);
        }
    }


    /**
     * 
     * A function that sends an HTTP request to the Kernel server to confirm that the server is up 
     * and the client is prepared to start the game.
     * 
     * It includes the token obtained from the login function in the request header for authentication purposes.
     * 
     */


    public void ready(){
        // Initializing the request
        String path = url + "/ready";
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-access-token", token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        
        // Sending the request
        try{
            ResponseEntity<String> response = restTemplate.exchange(path, HttpMethod.GET, requestEntity, String.class);
            
            if(response.getStatusCode().is2xxSuccessful()){
                System.out.println("ready");
            }
            else{
                JSONParser parser = new JSONParser();
                try {
                    JSONObject jsonResponse = (JSONObject) parser.parse(response.getBody());
                    System.out.println((String)jsonResponse.get("error"));
                    
                }catch (Exception e) {
                    System.out.println("Error with communicating with the server or Json parsing Error");
                }
                
                System.out.println("can't make a ready request");
                System.exit(0);
            }
        }catch(ResourceAccessException e){
            System.out.println("Connection Refused or Unavailable: " + e.getMessage());
            System.exit(0);
        }
    }


    //setters and getters 
    public int getMyPort(){
        return myPort;
    }
    public int getPassword(){
        return password;
    }
    public String getToken(){
        return token;
    }
    public String getServerIp(){
        return serverIp;
    }
    public String getServerPort(){
        return Integer.toString(serverPort);
    }
    public int getId(){
        return id;
    }
    public String getUrl(){
        return url;
    }





}
