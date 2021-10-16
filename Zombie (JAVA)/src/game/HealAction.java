package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * Special Action for Healing other Actors or yourself.
 * 
 * @author Tony Nguyen
 */
public class HealAction extends Action {
	private FoodItem item = null;
	//Assumptions
	//Note for now we have actor and target in case in the future we want to heal someone else in assignment 3
	//We could also directly just do actor.heal but in the advent of a behaviour with items i feel this is 
	//necessary -Tony
	
	/**
	 * The Person that needs to be healed
	 */
	protected Actor target;
	
	/**
	 * The amount of points you want to heal by
	 */
	protected int healAmount;
	
	/**
	 * Constructor for this action
	 * 
	 * @param target who do we want to heal
	 * @param healAmount how much do you want to heal by
	 */
	public HealAction(Actor target, int healAmount) {
		this.target = target;
		this.healAmount = healAmount;
	}

	/**
	 * Constructor for this action
	 * 
	 * @param target who do we want to heal
	 * @param healAmount how much do you want to heal by
	 */
	public HealAction(Actor target, int healAmount, FoodItem item) {
		this.target = target;
		this.healAmount = healAmount;
		this.item = item;
	}
	
	
	/**
	 * The execute does the thing it needs to
	 * 
	 * it will call the actor.heal() to do it
	 * 
	 * @return it should return who was healed and how much as a String to display
	 */
	public String execute(Actor actor,GameMap gameMap) {
		target.heal(healAmount);
		String result = target + " Healed for " + healAmount + " points.";
		if (this.item != null) {
		actor.removeItemFromInventory(this.item);
		}
		return result;
	}
	
	/**
	 * Just in case there is a menu option for this we will have it here
	 * 
	 * @return a menu option string which details what the action entails
	 */
	public String menuDescription(Actor actor) {
		return   target + " Heal?";
	}

}
