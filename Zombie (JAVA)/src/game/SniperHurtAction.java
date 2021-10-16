package game;

import edu.monash.fit2099.engine.*;

public class SniperHurtAction extends Action {
    private Actor target;
    private Sniper sniper;

    /**
     * contructor
     * @param newTarget
     * @param newsSniper
     */
    public SniperHurtAction(Actor newTarget, Sniper newsSniper){
        this.target =newTarget;
        this.sniper = newsSniper;
    }

    /**
     * this action hurts the target in the class 
     * 
     * @param Actor Map
     * @return String 
     */
    @Override
    public String execute(Actor actor, GameMap map){
        this.target.hurt(this.sniper.damage() * this.sniper.getAim());
        if (!this.sniper.getTarget().isConscious()){
            Item corpse;
            if(this.sniper.getTarget().getDisplayChar()=='Z') {corpse = new PortableItem("dead " + this.sniper.getTarget(), '^');}
            else {corpse =  new PortableItem("dead " + this.sniper.getTarget(), '%');}
            
            map.locationOf(this.sniper.getTarget()).addItem(corpse);
            
            Actions dropActions = new Actions();
            for (Item item : this.sniper.getTarget().getInventory())
                dropActions.add(item.getDropAction());
            for (Action drop : dropActions)		
                drop.execute(this.sniper.getTarget(), map);
            map.removeActor(this.sniper.getTarget());
            return this.target + " sniped. reduce health by " + (this.sniper.damage() * this.sniper.getAim()) + System.lineSeparator() + this.target + " is killed.";
        }
        return this.target + " sniped. reduce health by " + (this.sniper.damage() * this.sniper.getAim());
        
    }

    /**
     * menudecription to be shown
     */
    @Override
    public String menuDescription(Actor actor){
        return "shoot at " + this.sniper.getTarget() + ", aims : " + this.sniper.getAim();
    }
}