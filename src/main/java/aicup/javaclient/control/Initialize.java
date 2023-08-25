package aicup.javaclient.control;


import java.io.FileReader;
import java.security.SecureRandom;
import java.util.Map;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;





@Service
public class Initialize {


    
    private int password; // the password that I should recieve from the server to authenticate the server 
    private String token; // the token that I should send to the server in my requests for authentication
    private String serverIp; // the ip of the kernal server
    private int serverPort;  // the port of the kernal server
    private int myPort; // the port that I should run my api on    
    private int id; // the player id
    private final RestTemplate restTemplate;
    private String url; // the full server url
    private Game game; // the game controller

    private static Initialize initialize;

    // a contructor to initialize the needed variables
    private Initialize(){

        // TODO: disabling the proxy
        
        // init the password
        Random rand = new SecureRandom(); 
        password = rand.nextInt(100000000, 999999999);

        // init the server configuration
        JSONParser parser = new JSONParser();
        try {
           Object obj = parser.parse(new FileReader("src\\main\\java\\aicup\\javaclient\\config.json"));
           JSONObject jsonObject = (JSONObject)obj;
           serverIp = (String)jsonObject.get("server_ip");
           serverPort  = ((Long)jsonObject.get("server_port")).intValue();
           url = "http://" + serverIp +":"+ serverPort;
           
        } catch(Exception e) {
            System.out.println("Error in reading conf.json (the path of the file should be in src\\main\\java\\aicup\\javaclient\\config.json)");
            System.exit(0);
        }
        
        this.restTemplate = new RestTemplate();
    }
    public static Initialize getInsIance(){
        if(Objects.isNull(initialize)){
            initialize = new Initialize();
        }
        return initialize;
    }

    

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
        
        // Request
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

    public void ready(){
        //initializing the request
        String path = url + "/ready";
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-access-token", token);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        //sending the request
        try{
            ResponseEntity<String> response = restTemplate.exchange(path, HttpMethod.GET, requestEntity, String.class);
            
            if(response.getStatusCode().is2xxSuccessful()){
                System.out.println(response.getBody());
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
