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
		if (x != y)
			setEnabled(false);
		setBorder(BorderFactory.createEmptyBorder());
//		setBackground(Color.RED);
//		setForeground(Color.RED);
		this.size = size;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(size, size);
	}

//	@Override
//	public Color getBackground() {
//		if (!isEnabled())
//			return Color.GRAY;
//		if (isSelected())
//			return Color.RED;
//		else
//			return Color.GREEN.darker();
//	}

}
