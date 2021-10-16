package game;

import edu.monash.fit2099.engine.Action;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;

public class FertilizeAction extends Action{
    private Location target;    
	/**
	 * Constructor.
	 * 
	 * @param target the Actor to attack
	 */
	public FertilizeAction(Location target) {
		this.target = target;
	}

	@Override
	public String execute(Actor actor, GameMap map) {

        for(int i = 0; i < 10; i++){
            target.getGround().tick(target);
        }

        return actor + " has fertilize a crop.";
	}

	@Override
	public String menuDescription(Actor actor) {
		return actor + " fertilize crop.";
	}
}