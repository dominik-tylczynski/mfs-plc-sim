package pl.sapusers.mfsplc.sim;

import java.awt.Dimension;

import javax.swing.JToggleButton;

public class PlcCell extends JToggleButton {
	public Position pos;
	public int size;

	public PlcCell(int x, int y, int size) {
		super();

		this.pos = new Position(x, y);
		setEnabled(false);
		this.size = size;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(size, size);
	}
}
