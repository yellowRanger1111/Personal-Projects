package game;

import java.io.IOException;
import java.util.List;



import edu.monash.fit2099.engine.ActorLocations;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.GroundFactory;

/**
 * This is an extension onto the gameMap, we are doing this becasue we need to use it's tick method
 * 
 * Extends GameMap
 * 
 * @author tonynguyen
 *
 */
public class NewGameMap extends GameMap{
	
	/**
	 * These are our Variables
	 * MamboTeleport is a reference to a class that handles the appearance and dissappearance of mambo marie
	 * Endgame is a referance to a class that handles checking the end game conditions
	 */
	protected MamboTeleport MamboSummon = new MamboTeleport();
	protected EndGame endCheck = new EndGame();
	
	//We add a constructor to make it happy (we are only adding the constructor we need)
	/**
	 * This is one of the constructors we need to make it so we can instantiate like Game map
	 * it should just call super
	 * @param groundFactory the input of ground factory to help create the map (tells is whats dirt and stuff)
	 * @param lines the layout of the map
	 */
	public NewGameMap(GroundFactory groundFactory, List<String> lines) {
		super(groundFactory, lines);
		// TODO Auto-generated constructor stub
		
		
	}
	/**
	 * This is one of the constructors we need to make it so we can instantiate like Game map
	 * it should just call super
	 * @param groundFactory the input of ground factory to help create the map (tells us whats dirt and stuff)
	 * @param groundChar the character of the ground
	 * @param width the length from left to right of the map
	 * @param height the length from top to bottom of the map
	 */
	public NewGameMap(GroundFactory groundFactory, char groundChar, int width, int height) {
		super( groundFactory,  groundChar,  width,  height);
	}

	/**
	 * 
	 * This is one of the constructors we need to make it so we can instantiate like Game map
	 * it should just call super
	 * @param groundFactory the input of ground factory to help create the map (tells us whats dirt and stuff)
	 * @param mapFile the file to which holds the contents of our map
	 * @throws IOException In the advent that the map file is incorrect it will fail
	 */
	public NewGameMap(GroundFactory groundFactory, String mapFile) throws IOException {
		super(groundFactory,  mapFile);
	}
	
	//add onto the tick method to incorporate 
	@Override
	/**
	 * The Extended tick method, we call super to do what it has to and then does what we added
	 * We added method calls to do thier respective checking
	 */
	public void tick() {
		super.tick();
		
		//Checking if the game has ended and summoning MamboMarie
		checkEndGame(actorLocations);
		voodooSummoning();
	}
	
	//this checks the end conditions
	/**
	 * This method is designated to checking the end of the game.
	 * It calls checkEndConditions of the EndGame class
	 * @param actorLocations This is the location of all actors, we pass it so that it can check the game regarding ending it
	 */
	public void checkEndGame(ActorLocations actorLocations) {
		this.endCheck.checkEndConditions(actorLocations, this, MamboSummon.getMamboAlive());
	}
	 
	//this summons mambo marie
	/**
	 * This method is the one designated to trying to summon MamboMarie
	 * It calls trySummonMambo from MamboTeleport
	 * 
	 */
	public void voodooSummoning() {
		this.MamboSummon.trySummonMambo(this, widths.max(), heights.max());
		
	}
}
