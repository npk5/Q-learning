package ovh.npk.util;

import java.util.function.Function;

public class NumberSpace {

	private final double[] nums;

	public NumberSpace(Function<Double, Double> f, double lb, double ub, int n) {
		this.nums = new double[n];
		double diff = (ub - lb), deltaX = 1.0/(n - 1), x = 0;
		for (int i = 0; i < n; i++, x += deltaX)
			nums[i] = lb + f.apply(x) * diff;
	}
	
	public double get(int i) {
		return nums[i];
	}
}
