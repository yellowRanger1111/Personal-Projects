package game;

import edu.monash.fit2099.engine.*;
import java.util.*;

public class SniperFindTargetAction extends Action{
    private List<Actor> targets = new ArrayList<Actor>();
    private Sniper sniper;
    
    
    public SniperFindTargetAction(Sniper newSniper){
        this.sniper = newSniper;
    }

    /**
     * this action gets all Actor of Zombie/Undeads
     * 
     * @param Actor Map
     * @return String 
     */
    @Override
    public String execute(Actor actor, GameMap map){
        
        for (int x : map.getXRange()){
            for (int y : map.getYRange()){
                if(map.isAnActorAt(map.at(x, y))){
                    if(map.at(x, y).getActor().hasCapability(ZombieCapability.UNDEAD)){
                        this.targets.add(map.at(x, y).getActor());

                    }
                }
            }
        }
        return new SniperMenuAction(targets, sniper).execute(actor, map);
    }
    
    /**
     * menudecription to be shown
     */
    @Override 
    public String menuDescription(Actor actor) {
        // dont know what to say?
        return "Use Sniper";
    }
 
}