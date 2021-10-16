package game;

import java.util.*;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;

public class ShotgunDirectionAction extends Action{
    private String direction;
    private List<Location> hitLocations = new ArrayList<Location>();

    /**
	 * Constructor.
	 * 
	 * @param direction, where to shoot
	 */
    ShotgunDirectionAction(String direction){
        this.direction = direction;
    }
    
    /**
     * get locations after saved
     * @return a list<location>
     */
    public List<Location> getHitLocation(){
        return Collections.unmodifiableList(hitLocations);
    }


    /**
	 * when executed, this will get all location that got hit by the shotgun
	 * 
	 * @param actor,map
     * @return call another function
	 */
    @Override
    public String execute(Actor actor, GameMap map){
        Location now = map.locationOf(actor);
        if(this.direction == "up"){
            for (int i = -1; i <= 1; i++){
                for(int j = -1; j >= -3; j--){
                    //check if it is in the game map, add to lis
                    if(map.getXRange().contains(now.x() + i) && map.getYRange().contains(now.y() + j)){
                        this.hitLocations.add(map.at(now.x() + i, now.y() + j));
                    }
                }
                    
            }
        }
        else if(this.direction == "right"){
            for (int i = 1; i <= 3; i++){
                for(int j = -1; j <= 1; j++){
                    //check if it is in the game map, add to lis
                    if(map.getXRange().contains(now.x() + i) && map.getYRange().contains(now.y() + j)){
                        this.hitLocations.add(map.at(now.x() + i, now.y() + j));
                        }
                    }
                    
            }
        }
        else if(this.direction == "left"){
            for (int i = -1; i >= -3; i++){
                for(int j = -1; j <= 1; j++){
                    //check if it is in the game map, add to lis
                    if(map.getXRange().contains(now.x() + i) && map.getYRange().contains(now.y() + j)){
                        this.hitLocations.add(map.at(now.x() + i, now.y() + j));
                        }
                    }
                    
            }
        }
        else if(this.direction == "down"){
            for (int i = -1; i <= 1; i++){
                for(int j = 1; j <= 3; j++){
                    //check if it is in the game map, add to lis
                    if(map.getXRange().contains(now.x() + i) && map.getYRange().contains(now.y() + j)){
                        this.hitLocations.add(map.at(now.x() + i, now.y() + j));
                        }
                    }
                    
            }
        }

        return new ShotgunHitAction(this).execute(actor, map);
    }

    /**
     * menudecription to be shown
     */
    @Override
    public String menuDescription(Actor actor) {
		return actor + " shottie to " + this.direction;
	}
}