package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Plc {
// saved attributes
	private ArrayList<Point> positions;
	private Color color;
	private String name;

	public Plc(Point pos) {
		positions = new ArrayList<Point>();
		positions.add(new Point(pos.x, pos.y));
		this.name = Integer.valueOf(pos.x).toString() + "-" + Integer.valueOf(pos.y).toString(); // TO-DO
	}

	public Plc(Point pos, Color color) {
		this(pos);
		this.color = color;
	}

	public int getPositionsCount() {
		return positions.size();
	}

	public void move(int dir) {
		switch (dir) {
		case KeyEvent.VK_UP:
			for (Point pos : positions)
				pos.y--;

			break;

		case KeyEvent.VK_DOWN:
			for (Point pos : positions)
				pos.y++;
			
			break;
		case KeyEvent.VK_LEFT:
			for (Point pos : positions)
				pos.x--;
			
			break;
		case KeyEvent.VK_RIGHT:
			for (Point pos : positions)
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

	public void appendPosition(Point pos) {
		if (!positions.contains(pos))
			positions.add(new Point(pos.x, pos.y));
	}

	public void removePosition(Point pos) {
		positions.remove(pos);
	}

	public boolean containsPosition(Point pos) {
		return positions.contains(pos);
	}

	public int indexOf(Point pos) {
		return positions.indexOf(pos);
	}

	public ArrayList<Point> getPosition() {
		return positions;
	}
}
