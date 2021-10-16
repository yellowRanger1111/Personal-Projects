package game;

import java.util.*;

import edu.monash.fit2099.engine.*;



public class ShotgunHitAction extends Action {
    private List<Actor> actorsHit = new ArrayList<Actor>();
    private ShotgunDirectionAction actionNow;
    protected Random rand = new Random();
    
    public ShotgunHitAction(ShotgunDirectionAction now){
        this.actionNow = now;
    }

    /**
     * this action gets all the actors in all the location hits, 
     * then do damage to them. Miss chance = 1/4 
     * 
     * @param Actor Map
     * @return String 
     */
    @Override
    public String execute(Actor actor, GameMap map){
        for (Location location : this.actionNow.getHitLocation()){
            if(location.containsAnActor()){
                actorsHit.add(location.getActor());
            }
        }
        String returnString = "";
        for (Actor actorsNow : this.actorsHit){
            //minus the damage if hit
            if(rand.nextInt(101) >= 75){
                returnString += "shotgun misses the " + actorsNow + ".\n";
            }
            else{
                //is this returning the shotgun?
                int damage = actor.getWeapon().damage();
                actorsNow.hurt(damage);   
                returnString += "shotgun hit " + actorsNow + " by " + damage;
                
                if(!actorsNow.isConscious()){
                    Item corpse;
                    if(actorsNow.getDisplayChar()=='Z') {corpse = new PortableItem("dead " + actorsNow, '^');}
                    else {corpse =  new PortableItem("dead " + actorsNow, '%');}
                    
                    map.locationOf(actorsNow).addItem(corpse);
                    
                    Actions dropActions = new Actions();
                    for (Item item : actorsNow.getInventory())
                        dropActions.add(item.getDropAction());
                    for (Action drop : dropActions)		
                        drop.execute(actorsNow, map);
                    map.removeActor(actorsNow);
                    returnString += System.lineSeparator() + actorsNow + " is killed.";
                }
            }
        }

        return returnString;
    }

    /**
     * menudecription to be shown
     */
    @Override
    public String menuDescription(Actor actor) {
		return actor + " shottie hit" + this.actorsHit;
	}
}