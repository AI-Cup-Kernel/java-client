package aicup.javaclient.control;



import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import aicup.javaclient.PlayerCode;


@RestController
public class Controller {
    
    private Game game;
    private Initialize init;

    public Controller(){
        game = new Game();
        init = Initialize.getInstance();
    }
    
        
    @GetMapping("/init")
    public ResponseEntity<String> initializer(@RequestHeader("x-access-token") String tokenFromHeader){
        ResponseEntity<String> response = validateRequest(tokenFromHeader);
        if (response.getStatusCode() == HttpStatus.OK) {
            game.setMyTurn(true);
            System.out.println("initializer started");
            Thread thread = new Thread(() -> {
                PlayerCode.initializer(game); 
            });
           thread.start(); 
    }
        return validateRequest(tokenFromHeader);

    }

    @GetMapping("/turn")
    public ResponseEntity<String> turn(@RequestHeader("x-access-token") String tokenFromHeader){
        ResponseEntity<String> response = validateRequest(tokenFromHeader);
        if (response.getStatusCode() == HttpStatus.OK) {
            game.setMyTurn(true);
            System.out.println("turn started");
            Thread thread = new Thread(() -> {
                PlayerCode.turn(game); 
            });
           thread.start();
    }
        return validateRequest(tokenFromHeader);

    }

    
    @GetMapping("/end")
    public ResponseEntity<String> endTurn(@RequestHeader("x-access-token") String tokenFromHeader){
        ResponseEntity<String> response = validateRequest(tokenFromHeader);
        if (response.getStatusCode() == HttpStatus.OK) {
            game.setMyTurn(false);
            System.out.println("turn ended");
        }
        return validateRequest(tokenFromHeader);
    }

    
    @GetMapping("/kill")
    public ResponseEntity<String> shutdown(@RequestHeader("x-access-token") String tokenFromHeader){
        ResponseEntity<String> response = validateRequest(tokenFromHeader);
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("kill");
            System.exit(0);    
        } 
        return validateRequest(tokenFromHeader);
        

    }



    // a function to validate the token in the server requests
    private ResponseEntity<String> validateRequest(String tokenFromHeader){
        System.out.println(tokenFromHeader);
        if (tokenFromHeader.equals(Integer.toString(init.getPassword()))) {
            return ResponseEntity.ok("ok");
        }
        JSONObject errorObject = new JSONObject();
        errorObject.put("error", "Invalid token!");
        String errorMessage = errorObject.toJSONString();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);

    }



    
}
