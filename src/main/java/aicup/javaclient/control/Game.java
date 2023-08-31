package aicup.javaclient.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;



/**
 * Represents a class that controls interactions with the game server.
 */

public class Game {

    private String token;// the token that I should send to the server in my requests for authentication
    private String url; //The complete server URL.
    private boolean myTurn; // True if its the player turn
    private final RestTemplate restTemplate; // an object that manage the http requests that I send

    private MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();


    
    // A contructor to initialize the needed variables
    public Game(){
        Initialize init = Initialize.getInstance();
        token = init.getToken();
        url = init.getUrl();
        myTurn = false;
        restTemplate = new RestTemplate();
    }   

    /**
     * Retrieves a map of node owners.
     *
     * @return A map containing node IDs as keys and player IDs as values.
     */
    public Map<Integer, Integer> getOwners(){
        JSONObject jsonResponse = request("/get_owners",HttpMethod.GET);
        return jsonToIntMap(jsonResponse);

    }
    

    /**
     * Retrieves a map of the number of troops on each node.
     *
     * @return A map containing node IDs as keys and troop counts as values.
     */
    public Map<Integer, Integer> getNumberOfTroops(){
            
        JSONObject jsonResponse = request("/get_troops_count",HttpMethod.GET);
        return jsonToIntMap(jsonResponse);

    }


    /**
     * Retrieves the current state of the game.
     *
     * @return The state of the game.
     */
    public int getState(){
        JSONObject jsonResponse = request("/get_state",HttpMethod.GET);
        int state = ((Long) jsonResponse.get("state")).intValue();
        return state;    
    }


    /**
     * Retrieves the current turn number.
     *
     * @return The current turn number.
     */
    public int getTurnNumber(){
        JSONObject jsonResponse = request("/get_turn_number",HttpMethod.GET);
        int turn = ((Long) jsonResponse.get("turn_number")).intValue();
        return turn;    
    }


    /**
     * Retrieves a map of node adjacency relationships.
     *
     * @return A map containing node IDs as keys and lists of adjacent node IDs as values.
     */
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

    /**
     * Advances the game state to the next state.
     *
     * @return True if the state transition was successful, otherwise false.
     * @throws Exception If there is an error during the state transition.
     */ 
    public boolean nextState() throws Exception{
        JSONObject jsonResponse = request("/next_state",HttpMethod.GET);
        if(jsonResponse.containsKey("error")){
            throw new Exception((String)jsonResponse.get("error"));
        }
        else{
            return true;
        }
        
    }

    /**
     * Places a single troop on the specified node.
     *
     * @param nodeId The ID of the node where the troop will be placed.
     * @return True if the troop placement was successful, otherwise false.
     * @throws Exception If there is an error during the troop placement.
     */
    public boolean putOneTroop(int nodeId) throws Exception{
        formData.clear();
        formData.add("node_id",Integer.toString(nodeId)); 
        JSONObject jsonResponse = request("/put_one_troop", HttpMethod.POST);
        if(jsonResponse.containsKey("error")){
            throw new Exception((String)jsonResponse.get("error"));
        }
        else{
            return true;
        }
    }

    /**
     * Places a specified number of troops on the specified node.
     *
     * @param nodeId         The ID of the node where the troops will be placed.
     * @param numberOfTroops The number of troops to be placed.
     * @return True if the troop placement was successful, otherwise false.
     * @throws Exception If there is an error during the troop placement.
     */
    public boolean putTroop(int nodeId, int numberOfTroops) throws Exception{
        formData.clear();
        formData.add("node_id",Integer.toString(nodeId));
        formData.add("number_of_troops",Integer.toString(numberOfTroops));
        JSONObject jsonResponse = request("/put_troop", HttpMethod.POST);
        if(jsonResponse.containsKey("error")){
            throw new Exception((String)jsonResponse.get("error"));
        }
        else{
            return true;     
        }
    }
    
    /**
     * Retrieves the player's ID.
     *
     * @return The player's ID.
     */
    public int getPlayerId(){
        JSONObject jsonResponse = request("/get_player_id",HttpMethod.GET);
        int playerId = ((Long) jsonResponse.get("player_id")).intValue();
        return playerId;  
    }



    /**
     * Attack from one node to another with a specified fraction of troops.
     *
     * @param originNodeId  The ID of the attacking node.
     * @param targetNodeId  The ID of the target node.
     * @param fraction      The fraction of troops to be used in the attack.
     * @param fraction      The fraction of troops to be moved to the targetNode if the attack was succesful.
     * @return True if the attack was successful and the attacker won (won == 1), otherwise false.
     * @throws Exception If there is an error during the attack initiation.
     */
    public boolean attack(int originNodeId, int targetNodeId,float fraction,float moveFraction) throws Exception{
        formData.clear();
        formData.add("attacking_id",Integer.toString(originNodeId));
        formData.add("target_id",Integer.toString(targetNodeId));
        formData.add("fraction",Float.toString(fraction));
        formData.add("move_fraction",Float.toString(moveFraction));
        JSONObject jsonResponse = request("/attack", HttpMethod.POST);
        if(jsonResponse.containsKey("error")){
            throw new Exception((String)jsonResponse.get("error"));
        }
        else{
            int won =  ((Long) jsonResponse.get("won")).intValue();
            return won == 1;
        }
        
    } 

    /**
     * Moves a specified number of troops from one node to another.
     *
     * @param originNodeId       The ID of the source node.
     * @param destinationNodeId  The ID of the destination node.
     * @param numberOfTroops     The number of troops to be moved.
     * @return True if the troop movement was successful.
     * @throws Exception If there is an error during the troop movement.
     */
    public boolean moveTroop(int originNodeId, int destinationNodeId,float numberOfTroops) throws Exception{
        formData.clear();
        formData.add("source",Integer.toString(originNodeId));
        formData.add("destination",Integer.toString(destinationNodeId));
        formData.add("troop_count",Float.toString(numberOfTroops));
        JSONObject jsonResponse = request("/move_troop", HttpMethod.POST);
        if(jsonResponse.containsKey("error")){
            throw new Exception((String)jsonResponse.get("error"));
        }
        else{
            return true;
        }
        
    }

    /**
     * Retrieves a map of strategic nodes and their associated scores.
     *
     * @return A map containing node IDs as keys and scores as values.
     */
    public Map<Integer,Integer> getStrategicNodes(){
        JSONObject jsonResponse = request("/get_strategic_nodes",HttpMethod.GET);
        JSONArray scoreArray = (JSONArray) jsonResponse.get("score");
        JSONArray nodesArray = (JSONArray) jsonResponse.get("strategic_nodes");

        Map<Integer, Integer> responseMap = new HashMap<>();

        for (int i = 0; i < nodesArray.size(); i++) {
            int node = ((Long) nodesArray.get(i)).intValue();
            int score = ((Long) scoreArray.get(i)).intValue();
            responseMap.put(node, score);
        }
        return responseMap;
    }
    

    
    /**
     * Retrieves the number of troops that can be placed in the current turn.
     *
     * @return The number of troops available for placement.
     */
    public int getNumberOfTroopsToPut(){
        JSONObject jsonResponse = request("/get_number_of_troops_to_put",HttpMethod.GET);
        int troops = ((Long) jsonResponse.get("number_of_troops")).intValue();
        return troops; 
    }

    /**
     * Retrieves a list of node IDs that are reachable from the specified node.
     *
     * @param nodeId The ID of the source node.
     * @return A list of reachable node IDs.
     * @throws Exception If there is an error during the retrieval of reachable nodes.
     */
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
                int intValue = ((Long) value).intValue();
                responseList.add(intValue);
            }
            return responseList;
        }
    }


    /**
     * Initiates the process of fortifying a node with a specified number of troops.
     *
     * @param nodeId     The ID of the node to be fortified.
     * @param troopCount The number of troops to be added to the node.
     * @return True if the fortification process was successful, otherwise false.
     * @throws Exception If there is an error during the fortification process.
     */
    public boolean fort(int nodeId,int troopCount) throws Exception{
        formData.clear();
        formData.add("node_id",Integer.toString(nodeId));
        formData.add("troop_count",Integer.toString(troopCount));
        JSONObject jsonResponse = request("/fort", HttpMethod.POST);
        if(jsonResponse.containsKey("error")){
            throw new Exception((String)jsonResponse.get("error"));
        }
        else{
            return true;
        }
        
    } 

    /**
     * Retrieves a map of the number of fortification troops available on each node.
     *
     * @return A map containing node IDs as keys and the number of fortification troops as values.
     */
    public Map<Integer, Integer> getNumberOfFortTroops(){
            
        JSONObject jsonResponse = request("/get_number_of_fort_troops",HttpMethod.GET);
        return jsonToIntMap(jsonResponse);

    }



    /**
     * Sends an HTTP request to the server and processes the response.
     *
     * 
     * @param path       The path of the request.
     * @param httpMethod The HTTP method (GET or POST).
     * @return The JSON response from the server.
     */
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
                
                ResponseEntity<String> response = null;
                try{
                    response = restTemplate.exchange(fullUrl, httpMethod, requestEntity, String.class);
                }catch(HttpClientErrorException e){
                    response = new ResponseEntity<>(e.getResponseBodyAsString(), HttpStatus.BAD_REQUEST);
                }catch(Exception e) {
                    response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
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


    /**
     * Converts a JSON response to a map of integer keys and values.
     *
     * @param jsonResponse The JSON response to be converted.
     * @return A map containing integer keys and values.
     */
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
            
    

    //getters and setters
    public void setMyTurn(boolean myTurn){
        this.myTurn = myTurn;
    }
    public boolean getMyTurn(){
        return myTurn;
    }
}
