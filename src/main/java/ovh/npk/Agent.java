package ovh.npk;

import ovh.npk.util.Coordinates;

public class Agent {
	
	private final int x0, y0;
	
	private int x, y;
	private int actions;
	
	public Agent(int startX, int startY) {
		this.x = this.x0 = startX;
		this.y = this.y0 = startY;
	}
	
	public Agent() {
		this(0, 0);
	}
	
	public Coordinates doAction(Action a) {
		switch (a) {
			case UP -> y--;
			case DOWN -> y++;
			case LEFT -> x--;
			case RIGHT -> x++;
		}
		actions++;
		
		return coordinates();
	}
	
	public int getActions() {
		return actions;
	}
	
	public int reset() {
		int actions = this.actions;
		x = x0;
		y = y0;
		this.actions = 0;
		return actions;
	}

	public Coordinates coordinates() {
		return new Coordinates(x, y);
	}
}
