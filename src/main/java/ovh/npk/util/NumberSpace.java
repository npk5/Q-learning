package ovh.npk.util;

import java.util.function.Function;

public class NumberSpace {

	private final double[] nums;

	public NumberSpace(Function<Double, Double> f, double lb, double ub, int n) {
		this.nums = new double[n];
		double diff = (ub - lb);
		for (int i = 0; i < n; i++)
			nums[i] = lb + f.apply(i / (n - 1.0)) * diff;
	}

	public double get(int i) {
		return nums[i];
	}
}
