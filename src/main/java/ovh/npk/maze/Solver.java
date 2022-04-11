package ovh.npk.maze;

import lombok.AllArgsConstructor;

import ovh.npk.learn.EpsilonGreedy;
import ovh.npk.learn.QLearning;
import ovh.npk.util.NumberSpace;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@AllArgsConstructor
public class Solver extends Thread {
	
	private static final int MAX_ACTIONS = 1 << 15;
	
	private final double alpha, gamma, epsilon;
	private final int thread, trials, runs;
	private final NDMaze maze;
	
	@Override
	public void run() {
		Map<Integer, Integer> rewardVisits = new HashMap<>();
		List<Integer> best = new ArrayList<>();
		
		for (int run = 0; run < runs; run++) {
			NDAgent agent = new NDAgent(maze, 0);
			QLearning learn = new QLearning(alpha, gamma);
			EpsilonGreedy model = new EpsilonGreedy(maze, learn, agent);
			
			NumberSpace epsilons = new NumberSpace(Math::sqrt, epsilon, 0.0, trials);
			
			List<Integer> path = new ArrayList<>();
			path.add(agent.getS());
			
			int a, a_;
			int s = agent.getS();
			for (int trial = 0; agent.getActions() < MAX_ACTIONS && trial < trials;) {
				a = model.getAction(epsilons.get(trial));
				
				agent.doAction(a);
				int s_ = agent.getS();
				a_ = agent.getValidActions().stream()
						.max(Comparator.comparingDouble(e -> learn.getQ(s_, e))).orElseThrow();
				
				learn.update(s, a, maze.getR(s_), s_, a_);
				s = s_;
				
				path.add(s);
				
				if (maze.getR(s) > 0) {
					rewardVisits.merge(s, 1, Integer::sum);
					
					agent.reset();
					s = agent.getS();
					trial++;
					
					if (best.size() == 0 || path.size() < best.size())
						best = path;
					
					path = new ArrayList<>();
					path.add(s);
				}
			}
		}
		
		File dir = new File("./output/");
		if (dir.exists() || dir.mkdir()) {
			try (BufferedWriter f = Files.newBufferedWriter(Path.of("./output/thread%s.out".formatted(thread)))) {
				f.write(trials + " " + runs + " " + alpha + " " + gamma + " " + epsilon + "\n");
				for (int step : best) {
					for (int i : maze.toArr(step))
						f.write(i + " ");
					f.write('\n');
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		rewardVisits.forEach((r, v) -> System.out.format("(%.2f, %.2f, %.2f) %s: %d\n",
				alpha, gamma, epsilon, Arrays.toString(maze.toArr(r)), v));
	}
}
