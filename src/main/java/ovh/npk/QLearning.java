package ovh.npk;

import ovh.npk.util.Coordinates;

import java.util.*;

public class QLearning {
	
	private final double alpha, gamma;
	private final Map<Coordinates, Map<Action, Double>> Q = new HashMap<>();
	
	public QLearning(double alpha, double gamma) {
		this.alpha = alpha;
		this.gamma = gamma;
	}
	
	public double getQ(Coordinates c, Action a) {
		return Q.getOrDefault(c, Collections.emptyMap())
				.getOrDefault(a, 0.0);
	}
	
	public void setQ(Coordinates c, Action a, double q) {
		Q.computeIfAbsent(c, k -> new HashMap<>()).put(a, q);
	}
	
	/**
	 * Q(c0, a) = Q(c0, a) + α(r + γ Q_max(c, a_c) - Q(c0, a)).
	 * Q_max(c, a_c) is the max Q value between all valid actions at c.
	 * @param c0 previous location
	 * @param a last action
	 * @param m maze
	 * @param c current location
	 */
	public void updateQ(Coordinates c0, Action a, Maze m, Coordinates c) {
		double q = getQ(c0, a);
		double maxQ = m.getValidActions(c).stream()
				.mapToDouble(action -> getQ(c, action))
				.max().orElse(0.0);
		setQ(c0, a, q + alpha*(m.getR(c) + gamma*maxQ - q));
	}
}
