package aicup.javaclient.control;



import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import aicup.javaclient.PlayerCode;



/**
 * 
 * A class that manages the API endpoints of the client server. 
 * 
*/

@RestController
public class Controller {
    
    private Game game; // An instance of the game class that allows you to make modifications to the game.
    private Initialize init; // An instance of the Initialize class that provides access to the initialization information.

    // A contructor to initialize the needed variables
    public Controller(){
        game = new Game();
        init = Initialize.getInstance();
    }
    
    
    /**
     * 
     * An endpoint to handle the initialize state in the game.
     * 
     * It validates the token sent by the server by calling the validateRequest method. 
     * If the validation is successful, it creates a new thread for the PlayerCode initializer function
     * and then returns an acknowledgment to the server regarding its response.
     * 
     * @param tokenFromHeader the value of the x-access-token from the request header
     * 
     */

    @GetMapping("/init")
    public ResponseEntity<String> initializer(@RequestHeader("x-access-token") String tokenFromHeader){
        ResponseEntity<String> response = validateRequest(tokenFromHeader);
        if (response.getStatusCode() == HttpStatus.OK) {
            game.setMyTurn(true);
            System.out.println("initializer started");
            Thread thread = new Thread(() -> {
                try{
                    PlayerCode.initializer(game); 
                }catch(Exception e){
                    System.out.print("ERROR: ");
                    e.printStackTrace();
                }
            });
           thread.start(); 
    }
        return response;

    }


    /**
     * 
     * An endpoint to handle the turn state in the game.
     * 
     * It validates the token sent by the server by calling the validateRequest method. 
     * If the validation is successful, it creates a new thread for the PlayerCode turn function
     * and then returns an acknowledgment to the server regarding its response.
     * 
     * @param tokenFromHeader the value of the x-access-token from the request header
     *  
     */

    @GetMapping("/turn")
    public ResponseEntity<String> turn(@RequestHeader("x-access-token") String tokenFromHeader){
        ResponseEntity<String> response = validateRequest(tokenFromHeader);
        if (response.getStatusCode() == HttpStatus.OK) {
            game.setMyTurn(true);
            System.out.println("turn started");
            Thread thread = new Thread(() -> {
                try{
                    PlayerCode.turn(game); 
                }catch(Exception e){
                    System.out.print("ERROR: ");
                    e.printStackTrace();
                }
            });
           thread.start();
    }
        return response;

    }


    /**
     * 
     * An endpoint to handle the ending state in the game.
     * 
     * It validates the token sent by the server by calling the validateRequest method. 
     * If the validation is successful, its ends the player turn, and then returns an 
     * acknowledgment to the server regarding its response.
     * 
     * @param tokenFromHeader the value of the x-access-token from the request header
     */
    @GetMapping("/end")
    public ResponseEntity<String> endTurn(@RequestHeader("x-access-token") String tokenFromHeader){
        ResponseEntity<String> response = validateRequest(tokenFromHeader);
        if (response.getStatusCode() == HttpStatus.OK) {
            game.setMyTurn(false);
            System.out.println("turn ended");
        }
        return validateRequest(tokenFromHeader);
    }

    
    /**
     * 
     * An endpoint to manage the game's completion and shut down the client.
     * 
     * It validates the token sent by the server by calling the validateRequest method. 
     * If the validation is successful, it exits the app, and then returns an 
     * acknowledgment to the server regarding its response.
     * 
     * @param tokenFromHeader the value of the x-access-token from the request header
     */
    @GetMapping("/kill")
    public ResponseEntity<String> shutdown(@RequestHeader("x-access-token") String tokenFromHeader){
        ResponseEntity<String> response = validateRequest(tokenFromHeader);
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("kill");
            System.exit(0);    
        } 
        return validateRequest(tokenFromHeader);
        
    }



    /**
     * 
     * A function that validates the token from the Kernel server requests and returns an "OK"
     * response with a status code of 200 if the validation is successful, and an "unauthenticated" 
     * response with a status code of 401 if it is not.
     * 
     * @param tokenFromHeader the value of the x-access-token from the request header
     * 
     */
    private ResponseEntity<String> validateRequest(String tokenFromHeader){

        if (tokenFromHeader.equals(Integer.toString(init.getPassword()))) {
            return ResponseEntity.ok("ok");
        }
        JSONObject errorObject = new JSONObject();
        errorObject.put("error", "Invalid token!");
        String errorMessage = errorObject.toJSONString();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);

    }



    
}
