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
        System.out.println("test 1 competed");
        System.out.println(game.getNumberOfTroopsToPut());
        /*Map<Integer,Integer> m =  game.getStrategicNodes();
        for (Integer name: m.keySet()) {
            String key = name.toString();
            String value = m.get(name).toString();
            System.out.println(key + " " + value);
        }*/
        return "ok";
    }

    @GetMapping("/turn")
    public String turn(){

        System.out.println("test 2 competed");
        return "ok";
    }

    
    @GetMapping("/end")
    public String endTurn(){
        System.out.println("test 3 competed");
        return "ok";
    }

    
    @GetMapping("/kill")
    public String shutdown(){
        System.out.println("test 4 competed");
        return "ok";
    }

    




    // a function to validate the token in the server requests
    private boolean checkToken(){

        return true;
    }



    
}
