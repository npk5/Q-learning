package ovh.npk.maze;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class NDMaze {
	
	private final HashMap<Integer, List<Integer>> MEM = new HashMap<>();
	
	private final double[] R; // NaN if wall
	private final int[] SHAPE;
	private final int[] DIMS;
	
	private NDMaze(double[] r, int[] shape, int[] dims) {
		R = r;
		SHAPE = shape;
		DIMS = dims;
	}
	
	public static NDMaze fromFile(String path) throws IOException {
		// Assume file is formatted properly
		List<String> lines = Files.readAllLines(Path.of(path));
		
		// Dimensions
		String[] line = lines.get(0).split("\\D");
		int[] shape = new int[line.length];
		int[] dims = new int[line.length + 1];
		int length = dims[0] = 1;
		for (int i = 0; i < line.length; i++)
			dims[i + 1] = length *= shape[i] = Integer.parseInt(line[i]);
		
		double[] r = new double[length];
		
		// Data
		int size = 0;
		for (int i = 1; i < lines.size(); i++) {
			line = lines.get(i).trim().split("\\D");
			for (String s : line)
				r[size++] = s.equals("0") ? Double.NaN : -1;
		}
		
		return new NDMaze(r, shape, dims);
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
		for (int i = 0; i < SHAPE.length; i++) {
			if (arr[i] < 0 || arr[i] > SHAPE[i])
				return -1;
			s += arr[i] * DIMS[i];
		}
		return s;
	}
	
	public int[] toArr(int s) {
		int[] arr = new int[SHAPE.length];
		
		for (int i = SHAPE.length - 1; i >= 0; i--) {
			arr[i] = s / DIMS[i];
			if (arr[i] < 0 || arr[i] > SHAPE[i])
				return null;
			s %= DIMS[i];
		}
		return arr;
	}
	
	// Methods for NDAgent
	
	List<Integer> getValidActions(int[] S, int s) {
		if (isWall(s))
			return Collections.emptyList();
		
		return MEM.computeIfAbsent(s, k -> {
			List<Integer> validActions = new ArrayList<>();
			
			// For every dimension, check +/- possible
			for (int a = -SHAPE.length; a <= SHAPE.length; a++)
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
		return s + sgn*DIMS[dim];
	}
	
	private boolean check(int[] S, int s, int a) {
		int dim = Math.abs(a) - 1, sgn = Integer.signum(a);
		if (dim < 0 || dim >= SHAPE.length)
			return false;
		
		int s_ = S[dim] + sgn;
		return !(s_ < 0 || s_ >= SHAPE[dim] || isWall(s + sgn*DIMS[dim]));
	}
	
	private boolean isWall(int s) {
		return Double.isNaN(R[s]);
	}
}
