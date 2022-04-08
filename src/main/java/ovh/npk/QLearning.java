package ovh.npk;

import java.util.*;

public class QLearning {
	
	private final double alpha, gamma;
	private final Map<IntTuple, Map<Integer, Double>> Q = new HashMap<>();
	
	public QLearning(double alpha, double gamma) {
		this.alpha = alpha;
		this.gamma = gamma;
	}
	
	// SLOW
	public double getQ(IntTuple c, int a) {
		return Q.getOrDefault(c, Collections.emptyMap())
				.getOrDefault(a, 0.0);
	}
	
	public void setQ(IntTuple c, int a, double q) {
		Q.computeIfAbsent(c, k -> new HashMap<>()).put(a, q);
	}
	// ENDSLOW
	
	/**
	 * Q(c0, a) = Q(c0, a) + α(r + γ Q_max(c, a_c) - Q(c0, a)).
	 * Q_max(c, a_c) is the max Q value between all valid actions at c.
	 * @param c0 previous location
	 * @param a last action
	 * @param m maze
	 * @param c current location
	 */
	public void updateQ(IntTuple c0, int a, NDMaze m, IntTuple c) {
		double q = getQ(c0, a);
		double maxQ = m.getValidActions(c).stream()
				.mapToDouble(action -> getQ(c, action))
				.max().orElse(0.0);
		setQ(c0, a, q + alpha*(m.getR(c) + gamma*maxQ - q));
	}
}
