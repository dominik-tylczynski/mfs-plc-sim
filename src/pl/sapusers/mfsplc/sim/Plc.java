package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Plc {
// saved attributes
	private ArrayList<Position> positions;
	private Color color;
	private String name;

	public Plc(Position pos) {
		positions = new ArrayList<Position>();
		positions.add(new Position(pos.x, pos.y));
		this.name = Integer.valueOf(pos.x).toString() + "-" + Integer.valueOf(pos.y).toString(); // TO-DO
	}

	public Plc(Position pos, Color color) {
		this(pos);
		this.color = color;
	}

	public boolean hasOnePosition() {
		return (positions.size() == 1 ? true : false);
	}

	public void move(int dir) {
		switch (dir) {
		case KeyEvent.VK_UP:
			for (Position pos : positions)
				pos.y--;

			break;

		case KeyEvent.VK_DOWN:
			for (Position pos : positions)
				pos.y++;
			
			break;
		case KeyEvent.VK_LEFT:
			for (Position pos : positions)
				pos.x--;
			
			break;
		case KeyEvent.VK_RIGHT:
			for (Position pos : positions)
				pos.x++;
			
			break;
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void appendPosition(Position pos) {
		if (!positions.contains(pos))
			positions.add(new Position(pos.x, pos.y));
	}

	public void removePosition(Position pos) {
		positions.remove(pos);
	}

	public boolean containsPosition(Position pos) {
		return positions.contains(pos);
	}

	public int indexOf(Position pos) {
		return positions.indexOf(pos);
	}

	public ArrayList<Position> getPosition() {
		return positions;
	}
}
