package game;

import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;
/**
 * This is the class that is representative of the Vehicle, it is an Item That allows an Actor to move between Maps
 * @author tonynguyen
 *
 */
public class Vehicle extends Item{

	/**
	 * 
	 * This is the Constructor of The vehicle class.
	 * It will call the Super and then add onto it's Allowable actions a new TraverseAction, TraverseAction will take
	 * the Location of the destination and the name of it.
	 * 
	 * @param name This is a String which represents the Name of the Vehicle, It can be a Car or anything it wants
	 * @param displayChar This is a Char which will be what is displayed onto the Map
	 * @param portable A boolean Which tells us if it is portable 
	 * @param newPlace This Is the Parameter which tells us the Destination of Travelling with this Vehicle
	 * @param locationName This is the Name of the Location the Vehicle can travel to (eg. Town and such)
	 */
	public Vehicle(String name, char displayChar, boolean portable, Location newPlace, String locationName) {
		super(name, displayChar, portable);
		
		//Adding to the allowable actions
		allowableActions.add(new TraverseAction(newPlace, locationName));

	}
	
	

}
