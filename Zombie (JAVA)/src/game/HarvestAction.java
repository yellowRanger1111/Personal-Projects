package game;

import edu.monash.fit2099.engine.Action;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;

public class HarvestAction extends Action{
    private Location target;    
	/**
	 * Constructor.
	 * 
	 * @param target the Actor to attack
	 */
	public HarvestAction(Location target) {
		this.target = target;
	}

	@Override
	public String execute(Actor actor, GameMap map) {

        target.addItem(new FoodItem());
        target.getGround().removeCapability(DirtCapability.CROP_RIPE);
		target.getGround().addCapability(DirtCapability.NOCROP);
		//change symbol
        
        return actor + " has harvest a crop.";
	}

	@Override
	public String menuDescription(Actor actor) {
		return actor + " harvest crop.";
	}
}