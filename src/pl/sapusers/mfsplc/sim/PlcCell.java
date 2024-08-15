package pl.sapusers.mfsplc.sim;

import javax.swing.JToggleButton;

public class PlcCell extends JToggleButton {
	public Position pos;

	public PlcCell(int x, int y) {
		super();

		this.pos = new Position(x, y);
		setEnabled(false);
	}
}
