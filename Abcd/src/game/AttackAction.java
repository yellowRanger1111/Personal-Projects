package game;

import java.util.List;
import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Weapon;

/**
 * Special Action for attacking other Actors.
 */
public class AttackAction extends Action {

	/**
	 * The Actor that is to be attacked
	 */
	protected Actor target;
	/**
	 * Random number generator
	 */
	protected Random rand = new Random();

	/**
	 * Constructor.
	 * 
	 * @param target the Actor to attack
	 */
	public AttackAction(Actor target) {
		this.target = target;
		
	}

	//Here we have added stuff for bite and losing limbs + corpse production
	/**
	 * We added some additional things here such as Healing when the attack is bites or losing limbs if the target 
	 * did in fact lose a limb, Along with this we changed the type of corpse produced so that rise from the dead didn't
	 * affect zombies.
	 * 
	 * @return String it will Return a String of what has happened
	 */
	@Override
	public String execute(Actor actor, GameMap map) {

		Weapon weapon = actor.getWeapon();

		
		//Implementation of miss chance for bite
		if (weapon.verb().equals("bites") && rand.nextInt(101) >= 75) {
			return actor + " bite attack misses the " + target + ".";
		}
		
		//This is the normal chance of hitting for any other attack
		else if (rand.nextBoolean()) {
			return actor + " misses " + target + ".";
		}
		

		
		int damage = weapon.damage();
		String result = actor + " " + weapon.verb() + " " + target + " for " + damage + " damage.";
		
		
		target.hurt(damage);
		
		
		
		//zombie implementation of droping limbs onto map

		if(target.getDisplayChar() == 'Z') {
			
			//get inventory of zombie
			List<Item> inven = target.getInventory();
			for (int i = 0; i< inven.size();i++) {
				
				//see if inventory has limb (since we create one for hurt in zombie)
				if (inven.get(i).toString().equals("Zombie arm")) {
					//Place onto map
					
					ZombieArm item = new ZombieArm("Zombie arm",'a');
					map.locationOf(target).addItem(item);
					
					result+= System.lineSeparator() + target + " drops a limb";
					target.removeItemFromInventory(inven.get(i));
					break;
				}
				
			}
			
			
		}
		
			
		
		
		//Heal the zombie if bite, assumption is that only the zombie bites
		if (weapon.verb().equals("bites")) {
			HealAction lifeSteal = new HealAction(actor, 5);
			lifeSteal.execute(actor, map);
			result += System.lineSeparator() + actor + " is healed for 5";
		}
		
		
		
		
		if (!target.isConscious()) {
			Item corpse;
			if(target.getDisplayChar()=='Z') {corpse = new PortableItem("dead " + target, '^');}
			else {corpse =  new PortableItem("dead " + target, '%');}
			
			map.locationOf(target).addItem(corpse);
			
			Actions dropActions = new Actions();
			for (Item item : target.getInventory())
				dropActions.add(item.getDropAction());
			for (Action drop : dropActions)		
				drop.execute(target, map);
			map.removeActor(target);	
			
			result += System.lineSeparator() + target + " is killed.";
		}

		return result;
	}

	@Override
	/**
	 * menu option
	 * 
	 * 
	 */
	public String menuDescription(Actor actor) {
		return actor + " attacks " + target;
	}
}
