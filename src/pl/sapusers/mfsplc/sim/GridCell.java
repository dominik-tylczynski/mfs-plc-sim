package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.awt.Point;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

public class GridCell extends JLabel {
	public Point pos;
	public int size;

	public GridCell(int x, int y, int size, Color cellColor) {
		super("", JLabel.CENTER);
		setOpaque(true);

		this.pos = new Point(x, y);
		this.size = size;

		setBorder(BorderFactory.createEmptyBorder());
		setBackground(cellColor);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(size, size);
	}
}
