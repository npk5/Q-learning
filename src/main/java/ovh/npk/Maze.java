package ovh.npk;

import ovh.npk.util.Coordinates;

import java.io.*;
import java.util.*;

public class Maze implements Cloneable {
	
	private final State[][] maze;
	private final double[][] R;
	
	public Maze(File file) {
		int x, y;
		try (BufferedReader r = new BufferedReader(new FileReader(file))) {
			int[] shape = Arrays.stream(r.readLine().split(" "))
					.mapToInt(Integer::parseInt).toArray();
			y = shape[0];
			x = shape[1];
			if (x <= 0 || y <= 0)
				throw new IllegalStateException("Illegal maze size (%d, %d).".formatted(y, x));
			maze = new State[y][x];
			
			for (int row = 0; row < maze.length; row++) {
				String[] line = r.readLine().split(" ");
				for (int col = 0; col < maze[row].length; col++)
					maze[row][col] = line[col].equals("0") ? State.WALL : State.PATH;
			}
		} catch (IOException | NumberFormatException e) {
			throw new IllegalStateException(e.getMessage());
		}
		R = new double[y][x];
		for (double[] row : R)
			Arrays.fill(row, -1);
	}
	
	public List<Action> getValidActions(Coordinates c) {
		int x = c.x(), y = c.y();
		
		List<Action> validActions = new ArrayList<>();
		if (y > 0 && isPath(x, y - 1))
			validActions.add(Action.UP);
		if (y < maze.length - 1 && isPath(x, y + 1))
			validActions.add(Action.DOWN);
		if (x > 0 && isPath(x - 1, y))
			validActions.add(Action.LEFT);
		if (x < maze[y].length - 1 && isPath(x + 1, y))
			validActions.add(Action.RIGHT);
		return validActions;
	}
	
	private boolean isPath(int x, int y) {
		return maze[y][x] == State.PATH;
	}
	
	private boolean isPath(Coordinates c) {
		return isPath(c.x(), c.y());
	}
	
	public <E extends Number> void setR(Coordinates c, E r) {
		if (!isPath(c))
			throw new IllegalArgumentException("Reward may only be allocated to path.");
		
		R[c.y()][c.x()] = r.doubleValue();
	}
	
	public double getR(Coordinates c) {
		return R[c.y()][c.x()];
	}
	
	/**
	 * Shallow copy of Maze
	 * @return clone
	 */
	@Override
	public Maze clone() {
		try {
			return (Maze) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}
}
