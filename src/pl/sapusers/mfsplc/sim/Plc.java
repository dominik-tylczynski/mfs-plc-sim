package pl.sapusers.mfsplc.sim;

import java.util.ArrayList;

public class Plc {
	private ArrayList<Position> cells;

	public Plc(Position pos) {
		cells.add(pos);
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
