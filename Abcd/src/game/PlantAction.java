package game;

import edu.monash.fit2099.engine.Action;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;

public class PlantAction  extends Action {
   
    private Location target;    
	/**
	 * Constructor.
	 * 
	 * @param target the Actor to attack
	 */
	public PlantAction(Location target) {
		this.target = target;
	}

	@Override
	public String execute(Actor actor, GameMap map) {

        target.getGround().addCapability(DirtCapability.CROP_UNRIPE);
		target.getGround().removeCapability(DirtCapability.NOCROP);
		//change symbol
		

        return actor + " has planted a crop.";
	}

	@Override
	public String menuDescription(Actor actor) {
		return actor + " plant crop.";
	}
}
