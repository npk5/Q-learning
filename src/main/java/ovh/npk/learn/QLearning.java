package ovh.npk.learn;

import java.util.*;

public class QLearning {
	
	private final double alpha, gamma;
	private final Map<Integer, Map<Integer, Double>> Q = new HashMap<>();
	
	public QLearning(double alpha, double gamma) {
		this.alpha = alpha;
		this.gamma = gamma;
	}
	
	public double getQ(int s, int a) {
		return Q.getOrDefault(s, Collections.emptyMap())
				.getOrDefault(a, 0.0);
	}
	
	private void setQ(int s, int a, double q) {
		Q.computeIfAbsent(s, k -> new HashMap<>()).put(a, q);
	}
	
	/**
	 * Q(s, a) = Q(s, a) + α(r + γ Q_max(s', a') - Q(s, a)).
	 * Q_max(s_, a') is the max Q value between all valid actions at s'.
	 * @param s state
	 * @param a action
	 * @param r reward
	 * @param s_ next state
	 * @param a_ next action
	 */
	public void update(int s, int a, double r, int s_, int a_) {
		setQ(s, a, getQ(s, a) + alpha*(r + gamma*getQ(s_, a_) - getQ(s, a)));
	}
}
