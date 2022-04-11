package ovh.npk;

import ovh.npk.maze.NDMaze;
import ovh.npk.maze.Solver;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws IOException {
		// Parameters
		final double[] alphas = {0.9};
		final double[] gammas = {1.0};
		final double[] epsilons = {0.9};
		final int trials = 10000;
		final int runs = 10;
		
		// Load and initialize mazes
		List<NDMaze> mazes = List.of(
				NDMaze.fromFile("./data/easiest_maze.txt"),
				NDMaze.fromFile("./data/easy_maze.txt"),
				NDMaze.fromFile("./data/3d_maze.txt"),
				NDMaze.fromFile("./data/large_maze.txt")
		);
		
		Map<NDMaze, Map<Integer, ? extends Number>> rewards = Map.of(
				mazes.get(0), Map.of(
						mazes.get(0).toS(new int[]{9, 9}), 10,
						mazes.get(0).toS(new int[]{9, 0}), 5
				),
				mazes.get(1), Map.of(
						mazes.get(1).toS(new int[]{24, 14}), 10,
						mazes.get(1).toS(new int[]{0, 14}), 5
				),
				mazes.get(2), Map.of(
						mazes.get(2).toS(new int[]{4, 0, 2}), 10,
						mazes.get(2).toS(new int[]{4, 1, 2}), 150
				),
				mazes.get(3), Map.of(
						mazes.get(3).toS(new int[]{49, 11}), 10000,
						mazes.get(3).toS(new int[]{24, 3}), 5
				)
		);
		
		// Add rewards
		rewards.forEach((m, rs) -> rs.forEach(m::setR));
		
		
		// Grid search
		int i = 0;
		for (int m : List.of(3))
			for (double alpha : alphas)
				for (double gamma : gammas)
					for (double epsilon : epsilons)
						new Solver(alpha, gamma, epsilon, i++, trials, runs, mazes.get(m)).start();
	}
}
