package game;

import java.util.Random;


import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;


/**
 * Base class for any item that can be picked up and dropped.
 */
public class PortableItem extends Item {
	
	//We have this so we can add and remove, more specifically it's used to remember to remove
	
	/**
	 * constructor 
	 * 
	 * @param name name of the item
	 * @param displayChar what it looks like on the map
	 */
	public PortableItem(String name, char displayChar) {
		super(name, displayChar, true);
		
		
	}

	
	//The counter makes it so that you only have 5 - 10 turns to become a zombie, if beyond that time
	//Then no zombie is created from corpse.
	/**
	 * Variable for rising from the dead, counts the amount of turns
	 * 
	 */
	private int timeToRise = 0;
	

	/**
	 * I've modified tick so that it will if the portable item has a displayChar of '%' (a normal human corpse)
	 * it will increment timeToRise counter so that they will be able to rise on the right turn
	 * the rising is by removing the corpse and adding a zombie with the name dead "Name" zombie
	 */
	public void tick(Location currentLocation) {
		
		

		//since rise from the dead only works for those that are dead and there is a corpse 
		//since % is the used character for corpses, change here in case there is a change to char for corpse
		
		
		if (this.displayChar =='%'){
			
			//random factor to turning to zombie
			Random rand = new Random();
			
			//The chance to change into zombie is only within 5 to 10 turns
			if(timeToRise >= 5 && timeToRise <= 10  && rand.nextBoolean()) {
			
				currentLocation.removeItem(this);
				//Note that the Transformation won't be announced
				//Name will be dead character Zombie
				currentLocation.addActor(new Zombie(this.name +" Zombie"));
			}
			
			//This increments the time to rise
			else if (timeToRise <= 10) {
				timeToRise += 1;
			}
		}
		
		
		
		
		
		
	}
}
