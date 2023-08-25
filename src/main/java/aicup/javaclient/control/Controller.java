package aicup.javaclient.control;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controller {
    
    private Game game;

    public Controller(){
        game = new Game();
    }
    
    
    
    @GetMapping("/init")
    public String initializer(){

        return "ok";
    }

    @GetMapping("/turn")
    public String turn(){

        return "ok";
    }

    
    @GetMapping("/end")
    public String endTurn(){

        return "ok";
    }

    
    @GetMapping("/kill")
    public String shutdown(){

        return "ok";
    }

    




    // a function to validate the token in the server requests
    private boolean checkToken(){

        return true;
    }



    
}
