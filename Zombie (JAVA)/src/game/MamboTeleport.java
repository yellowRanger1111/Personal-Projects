package game;

import java.util.Random;

import edu.monash.fit2099.engine.GameMap;
/**
 * This class does the computations and need function calls regarding the reapearance and dissappearnce of MamboMarie
 * @author tonynguyen
 *
 */

public class MamboTeleport {
	//Mambo Alive is for a single mambo in the game
	/**
	 * Our Variables
	 * 
	 * mamboAlive this tells us if mambo is alive or not, it's static so that we don't summon more than once should
	 * for whatever reason we tick more than once, thus they would have shared information
	 * mamboOnMap same as above but does so for telling us if Mambo marie is on the map
	 * MamboMarie is a reference to MamboMarie the actor, we will try to add and remove her from the map
	 * Turns this will keep track of howmany turns have passed and will determine when she leaves
	 */
	protected static boolean mamboAlive = true;
	protected static boolean mamboOnMap = false;
	protected MamboMarie mambo = new MamboMarie();
	protected int Turns = 0;
	
	/**
	 * A getter for mamboAlive because she is part of the ending the game conditions
	 * 
	 * @return mamboAlive it will return the Boolean telling us if she is alive or not
	 */
	public boolean getMamboAlive() {
		return mamboAlive;
	}
	
	/**
	 * This method is what determines when she will appear and dissappear from the map as well as actually place her on the map
	 * 
	 * @param gameMap This is the map that Mambo Marie could potentially be added or removed from
	 * @param height this integer represents the length of the Map from top to bottom
	 * @param width this integer represents the length from left to right of the map
	 */
	public void trySummonMambo(GameMap gameMap,int height, int width) {
		
		//If she is alive (There is 1 MamboMarie so we assume that )
		if	(mamboAlive) {
			if (!mamboOnMap) {
				Random rand = new Random();
				
				if (rand.nextInt(100)< 5) {
					
					while(!mamboOnMap) {
						//we declare these variables here because they aren't used anywhere else but inside this while loop
						int x;
						int y;
						
						//deciding if the edge Mambo is spawining at
						if(rand.nextBoolean()) {
							
							// top or bottom edge
							if(rand.nextBoolean()) {
								y = 0;
							}
							else {y = width;}
							
							//along the edge
							x = rand.nextInt(height);
						
						}
						else {
							//left or right edge
							if(rand.nextBoolean()) {
								x = 0;
							}
							else {x = width;}
							
							//along the edge
							y = rand.nextInt(width);
						}
						
						//we check at that position if there is a person there or a wall there
						if (!gameMap.at(x, y).containsAnActor() && gameMap.at(x, y).getGround().canActorEnter(mambo)) {
							gameMap.at(x,y ).addActor(this.mambo);
							mamboOnMap = true;
						}
					}
				}
			
			}
			
			//We check the condition to despawn MamboMarie, Remember GameMaps all share the same actor location
			else if (this.Turns == 30  && gameMap.contains(this.mambo)) {
				gameMap.removeActor(this.mambo);
				this.Turns = 0; 
				mamboOnMap = false;
			}
			
			//We increment turns and check if she has died so then the game doesn't crash when we try to despawn her
			else {
				this.Turns += 1;
				
				//We change her status in the advent of her dying
				if (!gameMap.contains(mambo)) {
					mamboAlive = false;
				}
			}
		}
	}
}
