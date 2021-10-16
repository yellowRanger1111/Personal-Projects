package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
/**
 * This behaviour is what decides if Mambo would summon zombies or not
 * 
 * Implements Behaviour
 * @author tonynguyen
 *
 */
public class SummonZombieBehaviour implements Behaviour {

	/**
	 * Variables
	 * 
	 * summonTurnCounter keeps track of the turn so that we know when to summon
	 */
	private int summonTurnCounter;
	
	/**
	 * Constructor 
	 * it sets summonTurnCounter to 0
	 */
	public SummonZombieBehaviour() {
		this.summonTurnCounter = 0;
	}
	
	@Override
	/**
	 * This Method would get the action of summoning zombies or nothing
	 * 
	 * @param actor This is the actor that has this behaviour
	 * @param map This is the map to which the actor is on
	 * 
	 * @return SummonZombieAction the Action for summoning zombies, only if it is time though
	 * @return null this is if she can't summon anything as it's not time yet
	 */
	public Action getAction(Actor actor, GameMap map) {
		if (summonTurnCounter == 10) {
			this.summonTurnCounter = 0;
			return new SummonZombieAction();
			
		}
		else {
		this.summonTurnCounter += 1;
		return null;
		}
	}

}
