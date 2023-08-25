package aicup.javaclient.control;

import java.lang.reflect.Executable;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class Game {

    private String token;
    private String url;
    private boolean myTurn;
    private final RestTemplate restTemplate ;

    private MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();


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
        JSONObject jsonResponse = request("/get_owners",HttpMethod.GET);
        return jsonToIntMap(jsonResponse);

    }
    //tested
    public Map<Integer, Integer> getNumberOfTroops(){
            
        JSONObject jsonResponse = request("/get_troops_count",HttpMethod.GET);
        return jsonToIntMap(jsonResponse);

    }
    //tested
    public int getState(){
        JSONObject jsonResponse = request("/get_state",HttpMethod.GET);
        int state = ((Long) jsonResponse.get("state")).intValue();
        return state;    
    }


    //tested
    public int getTurnNumber(){
        JSONObject jsonResponse = request("/get_turn_number",HttpMethod.GET);
        int turn = ((Long) jsonResponse.get("turn_number")).intValue();
        return turn;    
    }
    //tested
    public Map<Integer,List<Integer>> getAdjacency(){
        JSONObject jsonResponse = request("/get_adj",HttpMethod.GET);
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
        JSONObject jsonResponse = request("/next_state",HttpMethod.GET);
        if(Objects.isNull(jsonResponse.get("error"))){
            System.out.println("Jooooon");
        }
    }

    public boolean putOneTroop(int nodeId) throws Exception{
        formData.clear();
        formData.add("node_id",Integer.toString(nodeId)); 
        JSONObject jsonResponse = request(token, HttpMethod.POST);
        if(jsonResponse.containsKey("error")){
            throw new Exception((String)jsonResponse.get("error"));
        }
        else{
            return true;
        }
    }
    public boolean putTroop(int nodeId, int numberOfTroops) throws Exception{
        formData.clear();
        formData.add("node_id",Integer.toString(nodeId));
        formData.add("number_of_troops",Integer.toString(numberOfTroops));
        JSONObject jsonResponse = request(token, HttpMethod.POST);
        if(jsonResponse.containsKey("error")){
            throw new Exception((String)jsonResponse.get("error"));
        }
        else{
            return true;
        }
    }
    //tested
    public int getPlayerId(){
        JSONObject jsonResponse = request("/get_player_id",HttpMethod.GET);
        int playerId = ((Long) jsonResponse.get("player_id")).intValue();
        return playerId;  
    }
    public boolean attack(int originNodeId, int targetNodeId,float fraction) throws Exception{
        formData.clear();
        formData.add("attacking_id",Integer.toString(originNodeId));
        formData.add("target_id",Integer.toString(targetNodeId));
        formData.add("fraction",Float.toString(fraction));
        JSONObject jsonResponse = request(token, HttpMethod.POST);
        if(jsonResponse.containsKey("error")){
            throw new Exception((String)jsonResponse.get("error"));
        }
        else{
            return true;
        }
        
    } 
    public boolean moveTroop(int originNodeId, int destinationNodeId,float numberOfTroops) throws Exception{
        formData.clear();
        formData.add("source",Integer.toString(originNodeId));
        formData.add("destination",Integer.toString(destinationNodeId));
        formData.add("troop_count",Float.toString(numberOfTroops));
        JSONObject jsonResponse = request(token, HttpMethod.POST);
        if(jsonResponse.containsKey("error")){
            throw new Exception((String)jsonResponse.get("error"));
        }
        else{
            return true;
        }
        
    }

    //tested
    public Map<Integer,Integer> getStrategicNodes(){
        JSONObject jsonResponse = request("/get_strategic_nodes",HttpMethod.GET);
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
        JSONObject jsonResponse = request("/get_number_of_troops_to_put",HttpMethod.GET);
        int troops = ((Long) jsonResponse.get("number_of_troops")).intValue();
        return troops; 
    }

    public List<Integer> getReachable(int nodeId) throws Exception{
        formData.clear();
        formData.add("node_id",Integer.toString(nodeId));
        JSONObject jsonResponse = request("/get_reachable", HttpMethod.POST);
        if(jsonResponse.containsKey("error")){
            throw new Exception((String)jsonResponse.get("error"));
        }
        else{
            JSONArray jsonArray = (JSONArray) jsonResponse.get("reachable");
            List<Integer> responseList = new ArrayList<>();
            for (Object value : jsonArray) {
                int intValue = Integer.parseInt((String) value);
                responseList.add(intValue);
            }
            return responseList;
        }
    }

    private JSONObject request(String path,HttpMethod httpMethod){
            //initializing the request
            String fullUrl = url + path;
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-access-token", token);
            HttpEntity<?> requestEntity;
            //GET request
            if(httpMethod==HttpMethod.GET){
                requestEntity = new HttpEntity<>(headers);
            }
            //POST request
            else{
                requestEntity = new HttpEntity<>(formData, headers);

            }      
            //sending the request
            try{
                ResponseEntity<String> response = restTemplate.exchange(fullUrl, httpMethod, requestEntity, String.class);
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
