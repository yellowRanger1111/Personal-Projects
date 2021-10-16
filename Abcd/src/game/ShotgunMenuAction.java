package game;

import edu.monash.fit2099.engine.*;



public class ShotgunMenuAction extends Action{
    private Menu menu = new Menu();

    /**
     * this action show the menu on where to shoot the shotgun
     * 
     * @param Actor Map
     * @return String 
     */
    @Override
    public String execute(Actor actor, GameMap map){
        Actions actions = new Actions();
        Display display = new Display();
        actions.add(new ShotgunDirectionAction("up"));
        actions.add(new ShotgunDirectionAction("down"));
        actions.add(new ShotgunDirectionAction("right"));
        actions.add(new ShotgunDirectionAction("left"));
        
        Action choosenAction = menu.showMenu(actor, actions, display); 
        return choosenAction.execute(actor, map);   
    }

    /**
     * menudecription to be shown
     */
    @Override
    public String menuDescription(Actor actor){
        return "Use Shotgun";
    }
}