package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * This is the Class that represents the action of quitting the game, it does this by removing the player/actor from the field
 * 
 * Extends Action
 * @author tonynguyen
 *
 */
public class QuitAction extends Action {
	/**
	 * This Prompt is what we will display to the console, it will detail why the game ended (basically for saying player quit)
	 */
	private String prompt;
	
	/**
	 * This is the constructor, it will save onto prompt the display we want
	 * @param prompt this is the string we want to display in the console and return in execute
	 */
	public QuitAction(String prompt) {
		this.prompt = prompt;
	}
	
	@Override
	/**
	 * This method will be what ends the game for the actor. This will remove the actor from the map and thus when world goes to see
	 * if player is up it won't continue
	 * 
	 * @param actor This is the actor we will remove from the map
	 * @param map this will be the map we remove actor from
	 * 
	 * @return String this string will be displayed on the console
	 */
	public String execute(Actor actor, GameMap map) {
		//remove player to end game
		map.removeActor(actor);
		
		return "Game has ended because " + prompt;
	}

	@Override
	/**
	 * This is the method which will return the string that will be displayed in the menu
	 * 
	 * @return String this string will be displayed onto the map
	 */
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return "Quit game";
	}

}
