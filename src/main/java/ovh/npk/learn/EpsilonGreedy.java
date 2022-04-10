package ovh.npk.learn;

import ovh.npk.maze.NDAgent;
import ovh.npk.maze.NDMaze;

import java.util.List;
import java.util.Random;

public record EpsilonGreedy(NDMaze m, QLearning q, NDAgent a) {
	
	private static final Random RAND = new Random();
	
	private int getRandomAction() {
		List<Integer> actions = a.getValidActions();
		return actions.get(RAND.nextInt(actions.size()));
	}
	
	private int getBestAction() {
		List<Integer> actions = a.getValidActions();
		double bestQ = actions.stream()
				.mapToDouble(action -> q.getQ(a.getS(), action))
				.max().orElseThrow();
		List<Integer> bestActions = actions.stream()
				.filter(action -> q.getQ(a.getS(), action) == bestQ).toList();
		return bestActions.get(RAND.nextInt(bestActions.size()));
	}
	
	public int getAction(double epsilon) {
		return RAND.nextDouble() < epsilon
				? getRandomAction()
				: getBestAction();
	}
	
}
