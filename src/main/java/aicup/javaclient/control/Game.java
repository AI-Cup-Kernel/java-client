package aicup.javaclient.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.simple.JSONArray;
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

    // TODO: handle output (errors status codes, ....)

    //tested
    public Map<Integer, Integer> getOwners(){
        JSONObject jsonResponse = request("/get_owners");
        return jsonToIntMap(jsonResponse);

    }
    //tested
    public Map<Integer, Integer> getNumberOfTroops(){
            
        JSONObject jsonResponse = request("/get_troops_count");
        return jsonToIntMap(jsonResponse);

    }
    //tested
    public int getState(){
        JSONObject jsonResponse = request("/get_state");
        int state = ((Long) jsonResponse.get("state")).intValue();
        return state;    
    }


    //tested
    public int getTurnNumber(){
        JSONObject jsonResponse = request("/get_turn_number");
        int turn = ((Long) jsonResponse.get("turn_number")).intValue();
        return turn;    
    }
    //tested
    public Map<Integer,List<Integer>> getAdjacency(){
        JSONObject jsonResponse = request("/get_adj");
        Map<Integer, List<Integer>> responseMap = new HashMap<>();
        for (Object keyObj : jsonResponse.keySet()) {
            String key = (String) keyObj;
            JSONArray valuesArray = (JSONArray) jsonResponse.get(key);
            List<Integer> valuesList = new ArrayList<>();
            for (Object valueObj : valuesArray) {
                valuesList.add(((Long) valueObj).intValue());
            }

            responseMap.put(Integer.parseInt(key), valuesList);
        }
        return responseMap;
    }



    //TODO: its not working im sending the request and getting the error reponse but the 
    //jsonResponse object is still null and on the kernal side im gettint unknown error message
    public void nextState(){
        JSONObject jsonResponse = request("/next_state");
        if(Objects.isNull(jsonResponse.get("error"))){
            System.out.println("Jooooon");
        }
    }

    public void putOneTroop(){

    }
    public void putTroop(){

    }
    //tested
    public int getPlayerId(){
        JSONObject jsonResponse = request("/get_player_id");
        int playerId = ((Long) jsonResponse.get("player_id")).intValue();
        return playerId;  
    }
    public void attack(){

    } 
    public void moveTroop(){

    }

    //tested
    public Map<Integer,Integer> getStrategicNodes(){
        JSONObject jsonResponse = request("/get_strategic_nodes");
        JSONArray scoreArray = (JSONArray) jsonResponse.get("score");
        JSONArray nodesArray = (JSONArray) jsonResponse.get("strategic_nodes");

        // Create a Map<Integer, Integer> to store the parsed values
        Map<Integer, Integer> responseMap = new HashMap<>();

        for (int i = 0; i < nodesArray.size(); i++) {
            int node = ((Long) nodesArray.get(i)).intValue();
            int score = ((Long) scoreArray.get(i)).intValue();
            responseMap.put(node, score);
        }
        return responseMap;
    }
    
    //tested
    public int getNumberOfTroopsToPut(){
        JSONObject jsonResponse = request("/get_number_of_troops_to_put");
        int troops = ((Long) jsonResponse.get("number_of_troops")).intValue();
        return troops; 
    }


    private JSONObject request(String path){
            //initializing the request
            String fullUrl = url + path;
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-access-token", token);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
           
            //sending the request
            try{
                ResponseEntity<String> response = restTemplate.exchange(fullUrl, HttpMethod.GET, requestEntity, String.class);
                //System.out.println(response.getBody());
                JSONParser parser = new JSONParser();
                try {
                    JSONObject jsonResponse = (JSONObject) parser.parse(response.getBody());
                    return jsonResponse;
                } catch (Exception e) {
                    System.out.println("Error with communicating with the server or Json parsing Error");
                }
                
            } catch (Exception e) {
                System.out.println("Connection Refused or Unavailable: " + e.getMessage());
            }
        return null;
    } 

    private Map<Integer,Integer> jsonToIntMap(JSONObject jsonResponse){
        Map<Integer, Integer> responseMap = new HashMap<>();
        for (Object key : jsonResponse.keySet()) {
        String keyString = (String) key;
        int keyInt = Integer.parseInt(keyString);
        int valueInt = ((Long) jsonResponse.get(key)).intValue();
        responseMap.put(keyInt, valueInt);
        }
        return responseMap;
    

    }   
}
