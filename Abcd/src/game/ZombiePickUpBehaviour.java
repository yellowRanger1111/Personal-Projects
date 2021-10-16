package game;

import java.util.List;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.PickUpItemAction;

/**
 * This class represents how zombies will pick up the weapons they walk over
 * 
 * @author tonynguyen
 *
 */
public class ZombiePickUpBehaviour implements Behaviour{

	//Assumptions
	//Note the zombie won't activly seek items, but it will pick them up if they walk over one
	//Also note that Zombies only pick up weapons 
	
	@Override
	/**
	 * Calling this will check if items are on the ground and then if they are a weapon pick it up else return null
	 * 
	 * @return null if no weapon return action to pickup if there is
	 */
	public Action getAction(Actor actor, GameMap map) {
		// TODO Auto-generated method stub
		Location location = map.locationOf(actor);
		List<Item> allItemsHere = location.getItems();
		if(allItemsHere.isEmpty()) {
			return null;
		}
		
		for (int i = 0; i < allItemsHere.size(); i++) {
	
			
			if(allItemsHere.get(i).asWeapon() !=null) {
				//if it isn't null then it is weapon
				Item item = allItemsHere.get(i);
				return new PickUpItemAction(item);
				
			}
			
		
			
		}
		
		
		//in case
		return null;
		
	}

}
