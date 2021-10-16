package game;

import edu.monash.fit2099.engine.WeaponItem;

import edu.monash.fit2099.engine.Actor;

public class Sniper extends WeaponItem{
    /**
     * A range weapon.
     * saves target and aim
     * 
     * @author ram
     */
    private Actor target = null;
    private int aim = 0;

    /**
     * constructor
     */
    public Sniper(){
        super("Sniper", ']', 45, "snipe");
    }

    /**
     * setter for target
     * @param newTarget
     */
    public void setTarget(Actor newTarget){
        this.target = newTarget;
    }

    /**
     * getter for target
     * @return
     */
    public Actor getTarget(){
        return this.target;
    }

    /**
     * setter aim
     * @param newAim
     */
    public void setAim(int newAim){
        this.aim = newAim;
    }
    
    /**
     * getter for aim
     * @return
     */
    public int getAim(){
        return this.aim;
    }
}