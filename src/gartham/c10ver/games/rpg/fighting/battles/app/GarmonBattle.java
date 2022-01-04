package gartham.c10ver.games.rpg.fighting.battles.app;

import java.util.Collection;

import gartham.c10ver.games.rpg.fighting.battles.api.Battle;
import gartham.c10ver.games.rpg.fighting.battles.api.ControlledBattle;
import gartham.c10ver.games.rpg.fighting.battles.api.Controller;
import gartham.c10ver.games.rpg.fighting.fighters.Fighter;

/**
 * <p>
 * This class augments the API featured by {@link Battle} by providing automatic
 * processing of moves.
 * </p>
 * <p>
 * In a normal {@link Battle}, it is the responsibility of the developer to
 * retrieve the fighter ready to make the next move and to perform that move by
 * modifying the {@link Battle}'s state. The developer must then call
 * {@link #act(int)} specifying the ticks that that move took. This is repeated
 * for every move made by a {@link Fighter} in the {@link Battle}.
 * </p>
 * <p>
 * This class uses {@link GarmonTeam team}s that have a Controller, which
 * specifies how a {@link GarmonFighter fighter} behaves (i.e., what move it
 * makes or what actions comprise its move) whenever it is a {@link Fighter}'s
 * turn. Specifically, whenever it is a {@link Fighter}'s turn to move, the
 * Controller of the {@link GarmonTeam team} to which that {@link Fighter}
 * belongs is invoked and is supplied relevant arguments to facilitate the
 * execution of that {@link Fighter}'s move.
 * </p>
 * <p>
 * With just the {@link Battle} class, code utilizing the {@link Battle} API
 * must interact directly with the {@link Battle} object to actually execute
 * each {@link Fighter}'s move, each time a {@link Fighter} makes a move. This
 * allows for great flexibility, but for cases in which such large extents of
 * flexibility are not needed (e.g. where the same behavior is used by teams
 * every time it is one of their {@link Fighter}'s turns), the abstractions this
 * class provides can prove more useful.
 * </p>
 * <p>
 * This class removes the reliance on calling code between each call to
 * {@link #act(int)}, and so can function wholly "on its own," after being
 * started, (by simply calling each {@link Fighter}'s {@link GarmonTeam team}'s
 * controller to perform the operations normally done by calling code in between
 * calls to {@link #act(int)}). As a result, this class provides multiple modes
 * of operation, including modes executing on their own, discrete threads.
 * (Please note that this class is not multi-thread safe; in the case of methods
 * which launch their own thread, they are expected to be the only thread to
 * interface with the {@link GarmonBattle} until its completion.)
 * </p>
 * 
 * @author Gartham
 *
 */
public class GarmonBattle extends ControlledBattle<GarmonFighter, GarmonTeam> {

	public GarmonBattle(Collection<GarmonTeam> teams) {
		super(teams);
	}

	public GarmonBattle(GarmonTeam... teams) {
		super(teams);
	}

	@Override
	protected Controller<GarmonFighter> getController(GarmonFighter fighter) {
		// Gets the controller from the team to which the fighter belongs.
		return getTeam(fighter).getController();
	}

	public void surrender(GarmonTeam team) {
		if (getRemainingTeams().contains(team))
			for (var f : team)
				f.damage(f.getHealth());
	}

	public void surrender(GarmonFighter fighter) {
		if (getBattleQueue().contains(fighter))
			fighter.damage(fighter.getHealth());
	}

}
