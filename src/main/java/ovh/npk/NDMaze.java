package ovh.npk;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class NDMaze {
	
	private final double[] R; // NaN if wall
	private final int[] shape;
	@Getter @Setter
	private IntTuple start;
	
	public NDMaze(String path) {
		try {
			List<String> lines = Files.readAllLines(Path.of(path));
			shape = Arrays.stream(lines.get(0).split(" "))
					.mapToInt(Integer::parseInt).toArray();
			
			if (Arrays.stream(shape).anyMatch(i -> i <= 0))
				throw new IllegalStateException("Illegal maze size %s."
						.formatted(Arrays.toString(shape)));
			
			R = new double[Arrays.stream(shape).reduce(1, (acc, i) -> acc * i)];
			
			int size = 0;
			for (int i = 1; i < lines.size(); i++) {
				double[] line = Arrays.stream(lines.get(i).trim().split(" "))
						.mapToDouble(s -> s.equals("0") ? Double.NaN : -1).toArray();
				if (size + line.length > R.length)
					throw new IllegalStateException("Illegal maze format: too much data.");
				System.arraycopy(line, 0, R, size, line.length);
				size += line.length;
			}
			if (size < R.length)
				throw new IllegalStateException("Illegal maze format: not enough data.");
		} catch (IOException | NumberFormatException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	public List<Integer> getValidActions(IntTuple c) {
		if (!isPath(getI(c)))
			throw new IllegalArgumentException("Illegal coordinates %s: no path at coordinates."
					.formatted(Arrays.toString(c.data())));
		
		List<Integer> validActions = new ArrayList<>();
		
		// For every dimension, check +/- possible
		for (int i = 1; i <= dims(); i++) {
			NDAgent.updateC(c, i);
			try {
				if (isPath(getI(c)))
					validActions.add(i);
			} catch (IllegalArgumentException ignored) {}
			
			NDAgent.updateC(c, -i);
			NDAgent.updateC(c, -i);
			try {
				if (isPath(getI(c)))
					validActions.add(-i);
			} catch (IllegalArgumentException ignored) {}
			
			NDAgent.updateC(c, i); // Back to original coordinates
		}
		return validActions;
	}
	
	private int getI(IntTuple c) {
		int[] arr = c.data();
		if (arr.length != dims())
			throw new IllegalArgumentException("Illegal dimensions %d (should be %d)."
					.formatted(arr.length, dims()));
		
		for (int i = 0; i < dims(); i++)
			if (arr[i] < 0 || arr[i] >= shape[i])
				throw new IllegalArgumentException("Illegal value %d for dimension %d (should be [0,%d)."
						.formatted(arr[i], i, shape[i]));
		
		int idx = arr[0];
		for (int i = 1; i < dims(); i++)
			idx += arr[i] * shape[i - 1];
		return idx;
	}
	
	private boolean isPath(int i) {
		return !Double.isNaN(R[i]);
	}
	
	public <E extends Number> void setR(IntTuple c, E r) {
		int i = getI(c);
		if (!isPath(i))
			throw new IllegalArgumentException("Illegal coordinates %s: no path at coordinates."
					.formatted(Arrays.toString(c.data())));
		
		R[i] = r.doubleValue();
	}
	
	public double getR(IntTuple c) {
		return R[getI(c)];
	}
	
	public int dims() {
		return shape.length;
	}
}
