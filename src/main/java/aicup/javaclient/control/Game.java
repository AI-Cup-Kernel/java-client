package aicup.javaclient.control;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class Game {

    private String token;
    private String url;
    private boolean myTurn;
    private final RestTemplate restTemplate ;


    public Game(){
        Initialize init = Initialize.getInsIance();
        token = init.getToken();
        url = init.getUrl();
        myTurn = false;
        restTemplate = new RestTemplate();
    }   

    



    public Map<Integer, Integer> getOwners(){
            //initializing the request
            String path = url + "/get_owners";
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-access-token", token);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
           
            //sending the request
            try{
                ResponseEntity<String> response = restTemplate.exchange(path, HttpMethod.GET, requestEntity, String.class);
                //System.out.println(response.getBody());
                //reading the json response
                JSONParser parser = new JSONParser();
                try {
                    JSONObject jsonResponse = (JSONObject) parser.parse(response.getBody());

                    // Convert JSON response to a HashMap
                    Map<Integer, Integer> responseMap = new HashMap<>();
                    for (Object key : jsonResponse.keySet()) {
                        String keyString = (String) key;
                        int keyInt = Integer.parseInt(keyString);
                        int valueInt = ((Long) jsonResponse.get(key)).intValue();
                        responseMap.put(keyInt, valueInt);
                    }
                
                    return responseMap;
                } catch (Exception e) {
                    System.out.println("Error with communicating with the server or Json parsing Error");
                }
                
            } catch (Exception e) {
                System.out.println("Connection Refused or Unavailable: " + e.getMessage());
            }
        return null;
        
    }
    

    public Map<Integer,Integer> ObjectMapToIntMap(JSONObject jsonResponse){
        Map<Integer, Integer> IntMap = new HashMap<>();
        
        //Converting the response to Integer Hashmap
        for (Object key : jsonResponse.keySet()) {
            String keyString = (String) key;
            int intValue = Integer.parseInt(keyString); 
                    
            String valueString = (String) jsonResponse.get(key);
            int integerValue = Integer.parseInt(valueString); 
                
            IntMap.put(intValue, integerValue);
        }
        return IntMap;         
    }
}
