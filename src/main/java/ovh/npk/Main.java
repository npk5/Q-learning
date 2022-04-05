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
		final int trials = 1000;
		final int runs = 10;
		
		// Load and initialize mazes
		Map<Maze, Map<Coordinates, ? extends Number>> mazes = Map.of(
			new Maze(new File("./data/easiest_maze.txt")), Map.of(
				new Coordinates(9, 9), 10,
				new Coordinates(9, 0), 5
			),
			new Maze(new File("./data/easy_maze.txt")), Map.of(
				new Coordinates(24, 14), 10,
				new Coordinates(0, 14), 5
			)
		);
		
		// Add rewards
		mazes.forEach((m, rs) -> rs.forEach(m::setR));
		
		// Grid search
		int i = 0;
		for (Maze m : mazes.keySet())
			for (double alpha : alphas)
				for (double gamma : gammas)
					for (double epsilon : epsilons)
						new Run(i++, alpha, gamma, epsilon, trials, runs, m.clone()).start();
	}
	
	
	private static class Run extends Thread {
		
		final double alpha, gamma, epsilon;
		final int thread, trials, runs;
		final Maze m;
		
		int[][] steps;
		
		Run(int thread, double alpha, double gamma, double epsilon, int trials, int runs, Maze m) {
			this.thread = thread;
			this.alpha = alpha;
			this.gamma = gamma;
			this.epsilon = epsilon;
			this.trials = trials;
			this.runs = runs;
			this.m = m;
			
			steps = new int[runs][trials];
		}
		
		@Override
		public void run() {
			Map<Coordinates, Integer> rewardVisits = new HashMap<>();

			for (int run = 0; run < runs; run++) {
				Agent r = new Agent();
				QLearning q = new QLearning(alpha, gamma);

				NumberSpace epsilons = new NumberSpace(Math::sqrt, epsilon, 0.0, trials);
				
				Action a;
				Coordinates c = r.coordinates();
				for (int trial = 0; r.getActions() < 30_000 && trial < trials;) {
					a = EpsilonGreedy.getAction(c, m, q, epsilons.get(trial));
					
					q.updateQ(c, a, m, c = r.doAction(a));
					
					if (m.getR(c) > 0) {
						rewardVisits.compute(c, (k, v) -> (v == null) ? 1 : v + 1);
						steps[run][trial++] = r.reset();
						c = r.coordinates();
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

			rewardVisits.forEach((r, v) -> System.out.println("(" + alpha + "," + gamma + "," + epsilon + ") " + r + ": " + v));
		}
	}
}
