package ovh.npk;

import ovh.npk.learn.EpsilonGreedy;
import ovh.npk.learn.QLearning;
import ovh.npk.maze.NDAgent;
import ovh.npk.maze.NDMaze;
import ovh.npk.util.*;

import java.io.*;
import java.util.*;

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
				NDMaze.fromFile("./data/3d_maze.txt")
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
						mazes.get(2).toS(new int[]{2, 1, 0}), 10,
						mazes.get(2).toS(new int[]{2, 0, 1}), 5
				)
		);
		
		// Add rewards
		rewards.forEach((m, rs) -> rs.forEach(m::setR));
		
		
		// Grid search
		int i = 0;
		for (NDMaze maze : mazes)
			for (double alpha : alphas)
				for (double gamma : gammas)
					for (double epsilon : epsilons)
						new Run(i++, alpha, gamma, epsilon, trials, runs, maze).start();
	}
	
	
	private static class Run extends Thread {
		
		final double alpha, gamma, epsilon;
		final int thread, trials, runs;
		final NDMaze maze;
		
		Run(int thread, double alpha, double gamma, double epsilon, int trials, int runs, NDMaze maze) {
			this.thread = thread;
			this.alpha = alpha;
			this.gamma = gamma;
			this.epsilon = epsilon;
			this.trials = trials;
			this.runs = runs;
			this.maze = maze;
		}
		
		@Override
		public void run() {
			Map<Integer, Integer> rewardVisits = new HashMap<>();

			for (int run = 0; run < runs; run++) {
				NDAgent agent = new NDAgent(maze, 0);
				QLearning learn = new QLearning(alpha, gamma);
				EpsilonGreedy model = new EpsilonGreedy(maze, learn, agent);

				NumberSpace epsilons = new NumberSpace(Math::sqrt, epsilon, 0.0, trials);
				
				int a, a_;
				int s = agent.getS();
				for (int trial = 0; agent.getActions() < 30_000 && trial < trials;) {
					a = model.getAction(epsilons.get(trial));
					
					agent.doAction(a);
					int s_ = agent.getS();
					a_ = agent.getValidActions().stream()
							.max(Comparator.comparingDouble(e -> learn.getQ(s_, e))).orElseThrow();
					
					learn.update(s, a, maze.getR(s_), s_, a_);
					s = s_;
					
					if (maze.getR(s) > 0) {
						rewardVisits.merge(s, 1, Integer::sum);
						
						agent.reset();
						s = agent.getS();
						trial++;
					}
				}
			}
			
//			File dir = new File("./output/");
//			if (dir.exists() || dir.mkdir()) {
//				try (BufferedWriter f = new BufferedWriter(new FileWriter("./output/" + thread + ".txt"))) {
//					f.write(trials + " " + runs + " " + alpha + " " + gamma + " " + epsilon + "\n");
//					for (int[] run : steps) {
//						for (int step : run)
//							f.write(step + " ");
//						f.write('\n');
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}

			rewardVisits.forEach((r, v) -> System.out.format("(%.2f, %.2f, %.2f) %s: %d\n",
					alpha, gamma, epsilon, Arrays.toString(maze.toArr(r)), v));
		}
	}
}
