package ovh.npk;

import ovh.npk.util.Coordinates;

import java.util.List;
import java.util.Random;

public class EpsilonGreedy {

	private static final Random RAND = new Random();
	
	private static Action getRandomAction(Coordinates c, Maze m) {
		List<Action> actions = m.getValidActions(c);
		return actions.get(RAND.nextInt(actions.size()));
	}
	
	private static Action getBestAction(Coordinates c, Maze m, QLearning q) {
		List<Action> actions = m.getValidActions(c);

		final double bestQ = actions.stream()
				.mapToDouble(action -> q.getQ(c, action))
				.max().orElse(Double.NaN);
		List<Action> bestActions = actions.stream()
				.filter(action -> q.getQ(c, action) == bestQ).toList();
		return bestActions.get(RAND.nextInt(bestActions.size()));
	}
	
	public static Action getAction(Coordinates c, Maze m, QLearning q, double epsilon) {
		return RAND.nextDouble() < epsilon
				? getRandomAction(c, m)
				: getBestAction(c, m, q);
	}

}
