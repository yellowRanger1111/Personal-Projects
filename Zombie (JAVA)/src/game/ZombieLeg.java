package game;

import edu.monash.fit2099.engine.DropItemAction;
import edu.monash.fit2099.engine.PickUpItemAction;


/**
 * This class is the ZombieLeg that will be dropped onto the map, it is also used in crafting
 * 
 * @author tonynguyen
 *
 */
public class ZombieLeg extends PortableItem{
	
	
	/**
	 * Constructor
	 * 
	 * @param name the name will always be "Zombie leg" with they way we use it for crafting and placing onto the map
	 * @param displayChar what character we use to display on the map
	 */
	public ZombieLeg(String name, char displayChar) {
		super(name, displayChar);
		// TODO Auto-generated constructor stub
	}


}
