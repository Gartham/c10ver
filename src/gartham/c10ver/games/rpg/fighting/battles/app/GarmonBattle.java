package gartham.c10ver.games.rpg.fighting.battles.app;

import java.util.Collection;

import gartham.c10ver.games.rpg.fighting.battles.api.Battle;
import gartham.c10ver.games.rpg.fighting.battles.api.ControlledBattle;
import gartham.c10ver.games.rpg.fighting.battles.api.Controller;
import gartham.c10ver.games.rpg.fighting.battles.api.Team;
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
 * {@link Battle}{@link #act(int)} specifying the ticks that the move took. This
 * is repeated for every move made by a {@link Fighter} in the {@link Battle}.
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

//	@Override
//	protected GarmonActionResult handleAction(GarmonBattleAction action, GarmonFighter fighter) {
//		switch (action.getType()) {
//		case ATTACK:
//			BigInteger attack = fighter.getAttack();
//			for (int i = 2; i < 13; i++)
//				if (rand.nextInt(i) == 0)
//					attack = attack.add(fighter.getAttack().divide(BigInteger.valueOf(i + 1)));
//			BigInteger dmg = max(BigInteger.ZERO, attack.subtract(action.getTarget().getDefense()));
//			action.getTarget().damage(dmg);
//			if (action.getTarget().isFainted())
//				remove(action.getTarget());
//			return new GarmonActionResult(50, action.getType(), dmg, action.getTarget());
//
//		case SKIP_TURN:
//			// Set ticks to be equal to the next OPPONENT in line + 1.
//			// We need some way to get the next opponent.
//			return new GarmonActionResult(getTicks(getNextNthOpponent(0, fighter)) + 1, action.getType(),
//					BigInteger.ZERO, null);
//
//		case SPECIAL_ATTACK:
//			var att = action.getSpecialAttack();
//			dmg = max(BigInteger.ZERO,
//					att.getPower().multiply(new BigDecimal(fighter.getAttack()))
//							.subtract(new BigDecimal(action.getTarget().getDefense())).setScale(0, RoundingMode.HALF_UP)
//							.toBigInteger());
//			action.getTarget().damage(dmg);
//			if (action.getTarget().isFainted())
//				remove(action.getTarget());
//			return new GarmonActionResult(att.getTicks(), action.getType(), dmg, action.getTarget());
//
//		default:
//			surrender(getTeam(fighter));
//			return new GarmonActionResult(100, action.getType(), BigInteger.ZERO, null);
//		}
//	}

}
