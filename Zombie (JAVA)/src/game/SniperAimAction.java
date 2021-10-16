package game;

import edu.monash.fit2099.engine.*;

public class SniperAimAction extends Action{
    private Sniper sniper;
    private Actor target;

    /**
     * construcktor
     * @param newSniper
     * @param newTarget
     */
    public SniperAimAction(Sniper newSniper, Actor newTarget){
        this.sniper = newSniper;
        this.target = newTarget;
    }

    /**
     * to reset aim, aim = 0
     */
    public void resetAim(){
        this.sniper.setAim(0);
    }


    /**
     * this action increases aim if the same target and set target and aim otherwise
     * 
     * @param Actor Map
     * @return String 
     */
    @Override
    public String execute(Actor actor, GameMap map){

        if(this.sniper.getTarget() == null){
            this.sniper.setAim(this.sniper.getAim() + 1);
            this.sniper.setTarget(this.target);
        }
        else if(this.sniper.getTarget() != this.target){
            this.resetAim();
            this.sniper.setAim(1);
            this.sniper.setTarget(this.target);

        }
        else {
            this.sniper.setAim(this.sniper.getAim() + 1);
            this.sniper.setTarget(this.target);
        }
        return actor + " aims at " + this.target +" "+ this.sniper.getAim()+ " times.";
    }


    //the string shown in menu
    @Override
    public String menuDescription(Actor actor){
        return "Snipe " + this.target + "?";
    }
}