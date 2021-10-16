package game;

import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.DoNothingAction;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.IntrinsicWeapon;

/**
 * A Zombie.
 * 
 * This Zombie is pretty boring.  It needs to be made more interesting.
 * 
 * @author ram
 *
 */
public class Zombie extends ZombieActor {
	
	protected int arms;
	
	protected int legs;
	
	/**
	 * Added 2 new behaviours to incorporate picking weapons up and zombies speaking
	 * 
	 *
	 */
	private Behaviour[] behaviours = {
			//new behaviours (note that the order is very specific)
			//2 behaviours created
			new ZombieSpeechBehaviour(),
			new ZombiePickUpBehaviour(),
			
			
			new AttackBehaviour(ZombieCapability.ALIVE),
			new HuntBehaviour(Human.class, 10),
			new WanderBehaviour()
	};

	public Zombie(String name) {
		super(name, 'Z', 100, ZombieCapability.UNDEAD);
		//change here/ add things here if you want zombies to have specified amounts of limbs
		this.arms = 2;
		this.legs = 2;
	}
	

	//Need to override hurt for limbs
	//Implementation of losing limbs
	/**
	 * Overwritten Hurt to deal with losing limbs and adding limbs to inventory so that AttackAction can see we 
	 * have lost some limbs
	 */
	@Override
	public void hurt(int points) {
		Random rand = new Random();
		
		//Decide if it will lose a limb
		if ((this.arms>0 || this.legs>0) && rand.nextInt(4)==0 ) {
			//we add limbs to inventory to see from attackaction if a limb has been lost
			
			//decide which limb
			if(this.arms> 0 && rand.nextBoolean()) {
				this.arms -= 1;
				this.inventory.add(new ZombieArm("Zombie arm", 'a'));	
				
			}
			//here means legs fall
			else if (this.legs>0) {
				this.legs -= 1;
				this.inventory.add(new ZombieLeg("Zombie leg",'l'));
				
			}
			//here if no legs meaning skip past the above
			else {
				this.arms -= 1;
				this.inventory.add(new ZombieArm("Zombie arm", 'a'));
			
			}
		
		}
		
		
		hitPoints -= points;
	}
	
	/**
	 * We have overwritten it to add bite chances as well as attack chances with the current amount of limbs
	 * 
	 * @return IntrinsicWeapon which should be bite or punch
	 */
	@Override
	public IntrinsicWeapon getIntrinsicWeapon() {
		//implementation of bite and amount of arms affecting attacks
		//I am going to assume bite is an intrinsic weapon
		
		Random rand = new Random();
			
		//generate chance to bit (0 - 100)
		int biteChance = rand.nextInt(101);
			
		//chance to bite at 50% with both arms up (we have the more than symbol just in case)
		if(biteChance >= 50 && this.arms>= 2) {
			return new IntrinsicWeapon(30, "bites");
			}
		
		//Chance to bite if you have 1 arm (75% since 50% to punch normally and has to be halved with 1 arm)
		else if(biteChance >= 25 && this.arms== 1) {
			return new IntrinsicWeapon(30, "bites");
		}
		
		//You can only bite if you have no arms
		else if(this.arms == 0) {
			return new IntrinsicWeapon(30, "bites");
		}
		
		//fail at chance to bite 
		else {
			return new IntrinsicWeapon(10, "punches");
			}
			
		
	}

	/**
	 * If a Zombie can attack, it will.  If not, it will chase any human within 10 spaces.  
	 * If no humans are close enough it will wander randomly.
	 * 
	 * @param actions list of possible Actions
	 * @param lastAction previous Action, if it was a multiturn action
	 * @param map the map where the current Zombie is
	 * @param display the Display where the Zombie's utterances will be displayed
	 */
	@Override
	public Action playTurn(Actions actions, Action lastAction, GameMap map, Display display) {

		
		
		for (Behaviour behaviour : behaviours) {
			Action action = behaviour.getAction(this, map);
			if (action != null)
				return action;
		}
		
		
		
		
		return new DoNothingAction();	
	}
}
