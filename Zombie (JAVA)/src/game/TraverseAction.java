package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
/**
 * This is the class representing the action of moving between the maps. It extends the action class.
 * 
 * Extends Action
 * @author tonynguyen
 *
 */
public class TraverseAction extends Action{
	/**
	 * Variables which save the destination in terms of Name and actual Location
	 * 
	 * moveTo a Location which holds the place we are moving to
	 * String is the Name of that location and is used for display
	 */
	//Our variables
	protected Location moveTo;
	protected String locationName;
	
	/**
	 * The constructor for Traverse Action, it will save to the variables what was given in.
	 * 
	 * @param newLocation This is a Location that will tell us where the action will take us to should we invoke it
	 * @param locationName A String that is the Name of where we would be going to
	 */
	public TraverseAction(Location newLocation, String locationName) {
		this.moveTo = newLocation;
		this.locationName = locationName;
	}

	@Override
	/**
	 * This execute method is what actually performs the moving of the Actor towards the targeted destination.
	 * 
	 * @param actor An Actor that will move to where we want it to be
	 * @param map	A GameMap, This GameMap will allowus to perform the moveActor() to get them to where we want
	 * 
	 * * @return String this string will be displayed on the console
	 */
	public String execute(Actor actor, GameMap map) {
		
		//Check if the place has anything blocking it, we move the actor if there isn't
		if (!moveTo.map().at(this.moveTo.x(), this.moveTo.y()).containsAnActor() && moveTo.map().at(this.moveTo.x(), this.moveTo.y()).canActorEnter(actor)) {
		map.moveActor(actor, moveTo);
		}
		
		//if there is then tell us that we can't do it
		else {
			return actor + "Can't go because something is in the way";}
		
		return actor + " has moved to " + this.locationName;
	}

	@Override
	/**
	 * This is the menuDescription it's what will show up in the menu if we have the ability to enact this action
	 * 
	 * @param actor The actor is not used in our case but it will still need to be passed to do this action as we have inherited it
	 * @return String this will be displayed onto the menu
	 */
	public String menuDescription(Actor actor) {
	
		return "Move to " + this.locationName;
	}

}
