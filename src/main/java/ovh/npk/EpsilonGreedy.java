package ovh.npk;

import java.util.List;
import java.util.Random;

public class EpsilonGreedy {

	private static final Random RAND = new Random();
	
	private static int getRandomAction(IntTuple c, NDMaze m) {
		List<Integer> actions = m.getValidActions(c);
		return actions.get(RAND.nextInt(actions.size()));
	}
	
	private static int getBestAction(IntTuple c, NDMaze m, QLearning q) {
		List<Integer> actions = m.getValidActions(c);

		final double bestQ = actions.stream()
				.mapToDouble(action -> q.getQ(c, action))
				.max().orElse(Double.NaN);
		List<Integer> bestActions = actions.stream()
				.filter(action -> q.getQ(c, action) == bestQ).toList();
		return bestActions.get(RAND.nextInt(bestActions.size()));
	}
	
	public static int getAction(IntTuple c, NDMaze m, QLearning q, double epsilon) {
		return RAND.nextDouble() < epsilon
				? getRandomAction(c, m)
				: getBestAction(c, m, q);
	}

}
