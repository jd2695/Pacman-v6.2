package pacman.entries.pacman;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import static pacman.game.Constants.DELAY;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	
	public MOVE getMove(Game game, long timeDue) 
	{
                Executor exec = new Executor();
		return myMove;
	}
}