package game;

import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
/**
 * This class represents the chance that zombies will spend there turn saying something
 * @author tonynguyen
 *
 */
public class ZombieSpeechBehaviour implements Behaviour{

	@Override
	/**
	 * This action will call zombie speech action if the chances do indeed say that the zombie will spend theur turn speaking
	 * 
	 * @return null if not speaking return ZombieSpeech action if yes speaking
	 */
	public Action getAction(Actor actor, GameMap map) {
		// TODO Auto-generated method stub
		
		Random rand = new Random();
		
		//10 percent to do speech
		int chanceAtSpeech = rand.nextInt(11);
		if(chanceAtSpeech == 0) {
			return new ZombieSpeechAction();
		}
		
		else {
		return null;
		}
	}

}
