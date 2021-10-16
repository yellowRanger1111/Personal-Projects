package game;

import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
/**
 * This class is representing the Summoning of Zombies
 * 
 * Extends Action
 * @author tonynguyen
 *
 */
public class SummonZombieAction extends Action{

	@Override
	/**
	 * This method is what will place 5 zombies at random places
	 * 
	 * @param actor The actor that is performing the action
	 * @param map The map it is happening on, we will place the zombies on this map
	 * 
	 * @return String The string to be displayed onto console telling us that Mambo has summon zombies
	 */
	public String execute(Actor actor, GameMap map) {
		// TODO Auto-generated method stub
		//Method Variables
		Random rand = new Random();
		int x;
		int y;
		int spawned = 0;
		
		//We loop with while as we don't know how many times we actually loop
		while(spawned != 5) {
			
			//random coordinates
			x = rand.nextInt(map.getXRange().max());
			y = rand.nextInt(map.getYRange().max());
			
			//We check for if there is an actor already there or if there is a wall
			if(!map.at(x, y).containsAnActor()&& map.at(x, y).getGround().canActorEnter(actor)) {
				//summon the zombie
			map.at(x, y).addActor(new Zombie("Mambo puppet Zombie"));
			spawned += 1;
			}
		}
		
		return "Mambo Summons her Puppets";
	}

	@Override
	/**
	 * The menu descrtiption that will be displayed
	 * 
	 * @param actor the actor performing the action and display on the menu for
	 * @return String it is null since we don't need to display a menu option
	 */
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return null;
	}

}
