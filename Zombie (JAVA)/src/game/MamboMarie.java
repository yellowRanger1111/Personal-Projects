package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.DoNothingAction;
import edu.monash.fit2099.engine.GameMap;
/**
 * This Class represents Mambo Marie and what she should be doing
 * 
 * Extends Zombieactor
 * @author tonynguyen
 *
 */
public class MamboMarie extends ZombieActor{
	/**
	 * Our Variable
	 * 
	 * behaviour should hold what she will be doing, SummonZombieBehaviour has been added
	 */
	private Behaviour[] behaviours = {
			new SummonZombieBehaviour(),
			new WanderBehaviour()
			
	};
	
	/**
	 * This is our constructor to create MamboMarie
	 */
	public MamboMarie() {
		super("Mambo Marie",'M', 100, ZombieCapability.ALIVE);
	}
	
	/**
	 * This is what processes what she would do
	 * 
	 * @param actions this is the list of actions that we process and see if she could do any
	 * @param lastAction this is for if there is a multi turn action
	 * @param map This is the current placement of the actor and will do according to it
	 * @param display This will display onto the console what she does i belive
	 */
	public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {

		for (Behaviour behaviour : behaviours) {
			Action action = behaviour.getAction(this, map);
			if (action != null)
				return action;
		}

		return new DoNothingAction();	
	}
}
