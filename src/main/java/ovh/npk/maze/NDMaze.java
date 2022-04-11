package ovh.npk.maze;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class NDMaze {
	
	private static final char WALL = '#';
	
	private final HashMap<Integer, List<Integer>> VALID_ACTIONS = new HashMap<>();
	
	private final double[] R; // NaN if wall
	private final int[] shape;
	private final int[] dims;
	
	private NDMaze(double[] R, int[] shape, int[] dims) {
		this.R = R;
		this.shape = shape;
		this.dims = dims;
	}
	
	/**
	 * The behavior of this method is undefined when
	 * provided with an incorrectly formatted file.
	 * @param path path to file
	 * @return maze
	 * @throws IOException not caught
	 */
	public static NDMaze fromFile(String path) throws IOException {
		List<String> lines = Files.readAllLines(Path.of(path));
		
		// Dimensions
		String[] split = lines.get(0).split("\\D");
		int[] shape = new int[split.length];
		int[] dims = new int[split.length + 1];
		int length = dims[0] = 1;
		for (int i = 0; i < split.length; i++)
			dims[i + 1] = length *= shape[i] = Integer.parseInt(split[i]);
		
		double[] R = new double[length];
		
		// Data
		int size = 0;
		for (int l = 1; l < lines.size(); l++) {
			String line = lines.get(l);
			for (int c = 0; c < line.length(); c++)
				R[size++] = line.charAt(c) == WALL ? Double.NaN : -1;
		}
		
		return new NDMaze(R, shape, dims);
	}
	
	public <E extends Number> boolean setR(int s, E r) {
		if (isWall(s))
			return false;
		
		R[s] = r.doubleValue();
		return true;
	}
	
	public double getR(int s) {
		return R[s];
	}
	
	public int toS(int[] arr) {
		int s = 0;
		for (int i = 0; i < shape.length; i++) {
			if (arr[i] < 0 || arr[i] >= shape[i])
				throw new IndexOutOfBoundsException("Index %d out of bounds for length %d"
						.formatted(arr[i], shape[i]));
			s += arr[i] * dims[i];
		}
		return s;
	}
	
	public int[] toArr(int s) {
		int[] arr = new int[shape.length];
		
		for (int i = shape.length - 1; i >= 0; i--) {
			arr[i] = s / dims[i];
			if (arr[i] < 0 || arr[i] >= shape[i])
				throw new IndexOutOfBoundsException("Index %d out of bounds for length %d"
						.formatted(arr[i], shape[i]));
			s %= dims[i];
		}
		return arr;
	}
	
	// Methods for NDAgent
	
	List<Integer> getValidActions(int[] S, int s) {
		if (isWall(s))
			return Collections.emptyList();
		
		return VALID_ACTIONS.computeIfAbsent(s, k -> {
			List<Integer> validActions = new ArrayList<>();
			
			// For every dimension, check +/- possible
			for (int a = -shape.length; a <= shape.length; a++)
				if (check(S, s, a))
					validActions.add(a);
			
			return Collections.unmodifiableList(validActions);
		});
	}
	
	int nextState(int[] S, int s, int a) {
		int dim = Math.abs(a) - 1, sgn = Integer.signum(a);
		if (!check(S, s, a))
			return -1;
		
		S[dim] += sgn;
		return s + sgn*dims[dim];
	}
	
	private boolean check(int[] S, int s, int a) {
		int dim = Math.abs(a) - 1, sgn = Integer.signum(a);
		if (dim < 0 || dim >= shape.length)
			return false;
		
		int s_ = S[dim] + sgn;
		return !(s_ < 0 || s_ >= shape[dim] || isWall(s + sgn*dims[dim]));
	}
	
	private boolean isWall(int s) {
		return Double.isNaN(R[s]);
	}
}
