package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;

/**
 * Class representing the Player.
 */
public class Player extends Human {

	private Menu menu = new Menu();
	
	

	/**
	 * Constructor.
	 *
	 * @param name        Name to call the player in the UI
	 * @param displayChar Character to represent the player in the UI
	 * @param hitPoints   Player's starting number of hitpoints
	 */
	public Player(String name, char displayChar, int hitPoints) {
		super(name, displayChar, hitPoints);
	}
	
	/**
	 * This method adds the crafting action to the players action, it will be called during the play turn methods, it checks 
	 * inventory and current list of actions
	 * 
	 * @param actions the list of actions we currently add
	 */
	public void canCraft(Actions actions) {
		for (int i = 0; i < this.inventory.size();i++) {
			//These determine if they are craftable
			if(this.inventory.get(i) instanceof ZombieArm || this.inventory.get(i) instanceof ZombieLeg) {
				actions.add(new CraftingAction(this.inventory.get(i), this.inventory.get(i).toString()));
			}
			
		}
	}

	@Override
	/**
	 * We have added to the playerTurn the needed actions it does this by adding to actions
	 * 
	 * @param actions is a List of Action that tells us what the player can do (We are now adding onto that) 
	 * @param lastAction in the advent of a 2 turn action we utilise this
	 * @param map this is the current map the actor is on
	 * @param Display from what i know this is what shows the menu options
	 * 
	 * @returns Action the action we have chosen to do
	 */
	public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {
		//Adding actions to allowable actions
		canCraft(actions);
		actions.add(new QuitAction("Player Has Quit"));
		
		for (int i = 0; i < this.inventory.size();i++) {
			//These determine if they are edible item (food)
			if(this.inventory.get(i) instanceof FoodItem) {
				actions.add(new HealAction(this, 20, (FoodItem) this.inventory.get(i)));
			}
			//determine if there is any shotgun
			else if(this.inventory.get(i) instanceof Shotgun){
				actions.add(new ShotgunMenuAction());
			}
			//determine if there is any sniper
			else if(this.inventory.get(i) instanceof Sniper){
				actions.add(new SniperFindTargetAction((Sniper)this.getInventory().get(i)));
			}
		}
		
		
		// Handle multi-turn Actions
		if (lastAction.getNextAction() != null)
			return lastAction.getNextAction();
		return menu.showMenu(this, actions, display);
	}
	
	/**
	 * hurting player, but use for resetting the function in sniper
	 * @param int
	 */
	@Override
	public void hurt(int points) {
		hitPoints -= points;
		for (int i = 0; i < this.inventory.size();i++){
			if(this.inventory.get(i) instanceof Sniper){
				Sniper snyper = (Sniper)(this.getInventory().get(i));
				snyper.setAim(0);
				snyper.setTarget(null);
			}
		}
	}
}
