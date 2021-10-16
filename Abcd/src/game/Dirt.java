package game;

import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;



/**
 * A class that represents bare dirt.
 */
public class Dirt extends Ground {

	private int ripeTime = 20;
	

	public Dirt() {
		super('.');
		this.addCapability(DirtCapability.NOCROP);
	}


	/**
	 * to change the disp when there is a crop.
	 */
	public void changeChar(char dispChar){
		this.displayChar = dispChar;
	}

	@Override
	public void tick(Location location) {
		
		if(this.hasCapability(DirtCapability.CROP_UNRIPE)){
			this.ripeTime --;
			changeChar('t');
			if(this.ripeTime == 0){
				this.addCapability(DirtCapability.CROP_RIPE);
				this.removeCapability(DirtCapability.CROP_UNRIPE);
				changeChar('T');
			}
		}	
		else if(this.hasCapability(DirtCapability.NOCROP)){
			changeChar('.');
		}
	}

}
