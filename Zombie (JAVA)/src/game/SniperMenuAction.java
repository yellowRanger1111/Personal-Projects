package game;
import java.util.*;
import edu.monash.fit2099.engine.*;



public class SniperMenuAction extends Action{
    private List<Actor> targets;
    private Sniper sniper;

    public SniperMenuAction(List<Actor> newTargets, Sniper newSniper){
        this.targets = newTargets;
        this.sniper = newSniper;
    }

    /**
     * this action show a mini menu on what can the actor do with the sniper 
     * 
     * @param Actor Map
     * @return String 
     */
    @Override
    public String execute(Actor actor, GameMap map){
        Menu menu = new Menu();
        Display display = new Display();
        Actions actions = new Actions();

        if(this.sniper.getAim() > 0){
            actions.add(new SniperHurtAction(this.sniper.getTarget(), this.sniper));
        }
        
        for (Actor target : targets){
            actions.add(new SniperAimAction(this.sniper, target));
        }

        Action choosenAction = menu.showMenu(actor, actions, display);

        return choosenAction.execute(actor, map);

    }

    /**
     * menudecription to be shown
     * not actually being used, but due to inheritance need to do it
     */
    @Override
    public String menuDescription(Actor actor){
        return "Use Sniper";
    }
}