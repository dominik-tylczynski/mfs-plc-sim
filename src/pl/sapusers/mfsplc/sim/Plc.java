package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.util.ArrayList;

public class Plc {
// saved attributes
	private ArrayList<Position> cells;
	private Color color;
	private String name;
	
	public Plc(Position pos) {
		cells = new ArrayList<Position>();
		cells.add(pos);
		this.name = Integer.valueOf(pos.x).toString() + "-" + Integer.valueOf(pos.y).toString();  //TODO
	}
	
	public Plc(Position pos, Color color) {
		this(pos);
		this.color = color;
	}	
	
	public boolean hasOneCell() {
		return (cells.size() == 1 ? true : false);
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

	public void appendCell(Position pos) {
		if (!cells.contains(pos))
			cells.add(pos);
	}
	
	public void removeCell(Position pos) {
		cells.remove(pos);
	}
	
	public boolean containsCell(Position pos) {
		return cells.contains(pos);
	}
	
	public int indexOf(Position pos) {
		return cells.indexOf(pos);
	}
	
	public ArrayList<Position> getCells() {
		return cells;
	}
}
