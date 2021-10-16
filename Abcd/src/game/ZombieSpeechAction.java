package game;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
/**
 * This class is for bringing back what line the zombie will say
 * @author tonynguyen
 *
 */
public class ZombieSpeechAction extends Action{

	protected List<String> quotes = Arrays.asList("screams 'HUMAN' ", "gurgles 'braaaiiiiinnss'" , "whispers 'thheeyyy... where...'");
	
	
	@Override
	/**
	 * Decides what line the zombie will be saying
	 * @return 1 of 3 Strings
	 */
	public String execute(Actor actor, GameMap map) {
		// TODO Auto-generated method stub
		Random rand = new Random();
		
		//since 3 quotes rand 3
		return actor + " " + quotes.get(rand.nextInt(3));
	}

	/**not needed but has to be here
	 * 
	 */
	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return null;
	}

}
