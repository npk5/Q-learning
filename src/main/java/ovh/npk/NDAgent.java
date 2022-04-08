package ovh.npk;

import lombok.Getter;

public class NDAgent {
	
	private final IntTuple c0;
	
	private IntTuple c;
	@Getter
	private int actions;
	
	public NDAgent(IntTuple c0) {
		this.c0 = c0.clone();
		this.c = c0.clone();
	}
	
	public void doAction(int a) {
		updateC(c, a);
		actions++;
	}
	
	public static void updateC(IntTuple c, int a) {
		int[] arr = c.data();
		if (a == 0 || Math.abs(a) > arr.length)
			throw new IllegalArgumentException("Illegal action %d (should be +/-[1,%d])."
					.formatted(a, arr.length));
		
		arr[Math.abs(a) - 1] += Integer.signum(a);
	}
	
	public IntTuple getC() {
		return c.clone();
	}
	
	public int reset() {
		int actions = this.actions;
		c = c0.clone();
		this.actions = 0;
		return actions;
	}
}
