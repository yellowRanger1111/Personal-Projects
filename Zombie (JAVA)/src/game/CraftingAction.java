package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.WeaponItem;

/**
 * This is the class representing the action of crafting
 * 
 * @author tonynguyen
 *
 */
public class CraftingAction extends Action{
	
	
	/**
	 * The item that is the crafting material
	 */
	protected Item item;
	
	/**
	 * This is the name of the item we are taking in
	 */
	protected String name;
	
	/**
	 * constructor
	 * 
	 * @param item the crafting material
	 * @param name the name of the crafting material
	 */
	public CraftingAction( Item item, String name) {
	this.item = item;
	this.name = name;
			
	}

	@Override
	/**
	 * Where the action actuall happens
	 * It checks the name so that it knows what it crafts into and removes that item from inventory and adds the 
	 * respective weapon to the inventory
	 * 
	 * @return it will return a string of what has been crafted 
	 */
	public String execute(Actor actor, GameMap map) {
		// TODO Auto-generated method stub
		//Assumption is that there are few items that we can create
		String result = "";
		
		//These are determining what can be crafted
		if (this.item instanceof ZombieArm) {
			WeaponItem weapon = new ZombieArmClub();
			
			actor.addItemToInventory(weapon);
			result = "Zombie Club";
		}
		
		else if(this.item instanceof ZombieLeg) {
			WeaponItem weapon = new ZombieLegMace();
			actor.addItemToInventory(weapon);
			result = "Zombie Mace";
			
		}
	
		
		
		actor.removeItemFromInventory(item);
		
		
		return result + " has been crafted";
	}

	

	@Override
	/**
	 * The menu option for crafting
	 * 
	 * @return it should return the Strings for which to craft
	 */
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		if (this.name.equals("Zombie arm")) {return " Craft Zombie Club from arm?";}
		else {return " Craft Zombie Mace from leg?" ;}
		
	}

}
