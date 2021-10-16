package game;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.ActorLocations;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
/**
 * This is a class that checks whether or not the game is in a situation where it needs to be ended.
 * @author tonynguyen
 *
 */
public class EndGame {
	//This is for displaying events that are more for events happening in the world such as 
	/**
	 * These are our variables that will be utilised throughout
	 * 
	 * 
	 * EVENT_DISPLAY will be a Display class and it's function is to be displaying events that happen unrealated to actors
	 * it is static because i don't want multiple display classes that do the same thing, it's final because it doesn't need to change
	 * ZombieWinMsg is a final variable used at multiple places thus is a final string to avoid literals, its for displying who wins
	 * HumanWinMsg is the same as Zombie win msg
	 * 
	 * 
	 */
	private static final Display EVENT_DISPLAY = new Display();
	private final String zombieWinMsg = "Zombies Win";
	private final String humanWinMsg = "Humans Win";

	/**
	 * This is the method utilised for checking the end of the game and performing the correct response and calls if we have met
	 * the condition.
	 * 
	 * @param actorLocations This is something that keeps track of the places of the actors, we use it to tell us who is alive
	 * @param map this is a map that will be utilised to execute the needed actions
	 * @param mamboStatus This is a boolean that tells us if mambo is alive or not which is part of the conditions to end
	 */
	public void checkEndConditions(ActorLocations actorLocations, GameMap map, Boolean mamboStatus) {
		//variables for seeing who won
				Boolean zombieWin = true;
				Boolean humanWin = true;
				
				//a reference to the player
				Actor player = null;
				
				//This iterating through the list of actors will check if there is a zombie or not
				for (Actor actor: actorLocations){
					
					//We need to use instanceof to differentiate between the actors
					//it seems poor but in this case we need to go down the line to subclasses rather than up to super, 
					//thus this doesn't go against polymorphism, it would go against polymorphism if i needed to invoke any of their methods
					if (actor instanceof Player) {
						player = actor;
					}
					else if(actor instanceof Zombie ) {
						humanWin = false;
					}
					
					else if (actor instanceof Human) {
						zombieWin = false;
					}
					
					
				}
				//if the player quit the game
				if (player == null) {
					// In the advent that player quits we do nothing here so that the next two won't be invoked
				}
				//Quit game if zombies win
				else if (zombieWin ) {
					// i may remove the  display options
					//Horrible code since display but this is one of my gripes with the engine class
					EVENT_DISPLAY.println(this.zombieWinMsg);
					new QuitAction(this.zombieWinMsg).execute(player, map);
				}
				//Quit game if humans win and Mambo is dead
				else if (humanWin && !mamboStatus) {
					//The displaying is only for only the action of the actors
					//note should move this so the gamemap itself would have the actor (we can do this later)
					EVENT_DISPLAY.println(this.humanWinMsg);
					new QuitAction(this.humanWinMsg).execute(player, map);
				}
				
			
	}
}
