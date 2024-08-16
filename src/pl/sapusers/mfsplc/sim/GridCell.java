package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JToggleButton;

public class GridCell extends JToggleButton {
	public Position pos;
	public int size;

	public GridCell(int x, int y, int size, Color cellColor) {
		super();

		this.pos = new Position(x, y);
		setEnabled(false);
		setBorder(BorderFactory.createEmptyBorder());
		setBackground(cellColor);
		this.size = size;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(size, size);
	}
	
}
	