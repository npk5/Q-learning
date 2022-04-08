package ovh.npk;

import ovh.npk.util.*;

import java.io.*;
import java.util.*;

public class Main {

	public static void main(String[] args) {
		// Parameters
		final double[] alphas = {0.9};
		final double[] gammas = {1.0};
		final double[] epsilons = {0.9};
		final int trials = 500;
		final int runs = 10;
		
		// Load and initialize mazes
		List<NDMaze> mazes = List.of(
				new NDMaze("./data/easiest_maze.txt"),
				new NDMaze("./data/easy_maze.txt")
		);
		
		Map<NDMaze, Map<IntTuple, ? extends Number>> rewards = Map.of(
				mazes.get(0), Map.of(
						new IntTuple(9, 9), 10,
						new IntTuple(9, 0), 5
				),
				mazes.get(1), Map.of(
						new IntTuple(24, 14), 10,
						new IntTuple(0, 14), 5
				)
		);
		
		// Add rewards
		rewards.forEach((m, rs) -> rs.forEach(m::setR));
		
		// Set starting positions
		Map<NDMaze, IntTuple> start = Map.of(
				mazes.get(0), new IntTuple(new int[mazes.get(0).dims()]),
				mazes.get(1), new IntTuple(new int[mazes.get(1).dims()])
		);
		
		// Add starting positions
		start.forEach(NDMaze::setStart);
		
		// Grid search
		int i = 0;
//		for (NDMaze maze : mazes)
			for (double alpha : alphas)
				for (double gamma : gammas)
					for (double epsilon : epsilons)
						new Run(i++, alpha, gamma, epsilon, trials, runs, mazes.get(0)).start();
	}
	
	
	private static class Run extends Thread {
		
		final double alpha, gamma, epsilon;
		final int thread, trials, runs;
		final NDMaze maze;
		
		int[][] steps;
		
		Run(int thread, double alpha, double gamma, double epsilon, int trials, int runs, NDMaze maze) {
			this.thread = thread;
			this.alpha = alpha;
			this.gamma = gamma;
			this.epsilon = epsilon;
			this.trials = trials;
			this.runs = runs;
			this.maze = maze;
			
			steps = new int[runs][trials];
		}
		
		@Override
		public void run() {
			Map<IntTuple, Integer> rewardVisits = new HashMap<>();

			for (int run = 0; run < runs; run++) {
				NDAgent r = new NDAgent(maze.getStart());
				QLearning q = new QLearning(alpha, gamma);

				NumberSpace epsilons = new NumberSpace(Math::sqrt, epsilon, 0.0, trials);
				
				int a;
				IntTuple c = r.getC(), cNext = r.getC();
				for (int trial = 0; r.getActions() < 30_000 && trial < trials;) {
					a = EpsilonGreedy.getAction(c, maze, q, epsilons.get(trial));
					
					r.doAction(a);
					NDAgent.updateC(cNext, a);
					q.updateQ(c, a, maze, cNext);
					NDAgent.updateC(c, a);
					
					if (maze.getR(c) > 0) {
						rewardVisits.merge(c, 1, Integer::sum);
						steps[run][trial++] = r.reset();
						c = r.getC();
						cNext = r.getC();
					}
				}
			}
			
			File dir = new File("./output/");
			if (dir.exists() || dir.mkdir()) {
				try (BufferedWriter f = new BufferedWriter(new FileWriter("./output/" + thread + ".txt"))) {
					f.write(trials + " " + runs + " " + alpha + " " + gamma + " " + epsilon + "\n");
					for (int[] run : steps) {
						for (int step : run)
							f.write(step + " ");
						f.write('\n');
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			rewardVisits.forEach((r, v) -> System.out.format("(%.2f, %.2f, %.2f) %s: %d\n",
					alpha, gamma, epsilon, Arrays.toString(r.data()), v));
		}
	}
}
