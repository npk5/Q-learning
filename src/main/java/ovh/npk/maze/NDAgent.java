package ovh.npk.maze;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class NDAgent {
	
	private final NDMaze m;
	private final int[] S0;
	private final int s0;
	private final int[] S;
	
	@Getter private int s;
	@Getter private int actions;
	
	public NDAgent(NDMaze m, int s0) {
		this.m = m;
		this.s0 = s0;
		this.S0 = m.toArr(s0);
		s = s0;
		S = Arrays.copyOf(S0, S0.length);
	}
	
	public List<Integer> getValidActions() {
		return m.getValidActions(S, s);
	}
	
	/**
	 * Call only with action returned from {@link NDMaze#getValidActions(int[], int)}.
	 * @param a action
	 * @return true if action executed, false otherwise
	 */
	public boolean doAction(int a) {
		int s_ = m.nextState(S, s, a);
		if (s == -1)
			return false;
		
		s = s_;
		actions++;
		return true;
	}
	
	public void reset() {
		s = s0;
		System.arraycopy(S0, 0, S, 0, S0.length);
		this.actions = 0;
	}
}
