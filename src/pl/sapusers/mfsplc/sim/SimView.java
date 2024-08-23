package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import pl.sapusers.mfsplc.Configurator;

public class SimView extends JPanel {
	public static final Border BORDER_UP = BorderFactory.createBevelBorder(BevelBorder.RAISED);
	public static final Border BORDER_DOWN = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	public static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder();

	private Configurator configurator;
	private SimController controller;
	private Color cellColor;
//	private HashMap<Position, GridCell> cells;
	GridCell[][] cells;

	public SimView(Configurator configurator, SimController controller) {
		super();

		this.configurator = configurator;
		this.controller = controller;

		cellColor = controller.getModel().getBackgroundColor();

		setLayout(new GridBagLayout());
		setBackground(cellColor.darker());

//		cells = new HashMap<Position, GridCell>();
		cells = new GridCell[configurator.getGridSizeX()][configurator.getGridSizeY()];

		GridBagConstraints gbc = new GridBagConstraints();
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[x].length; y++) {
				gbc.gridx = x;
				gbc.gridy = y;
				gbc.insets = new Insets(1, 1, 1, 1);

				GridCell cell = new GridCell(x, y, configurator.getCellSize(), cellColor);
				cells[x][y] = cell;

				cell.addMouseListener(controller);
				add(cell, gbc);
			}
		}
	};

	public void zoomIn() {
		int max = getComponentCount();

		for (int i = 0; i < max; i++) {
			GridCell cell = (GridCell) getComponent(i);
			cell.size += configurator.getZoomStep();
		}
		revalidate();
		repaint();
	}

	public void zoomOut() {
		int max = getComponentCount();

		for (int i = 0; i < max; i++) {
			GridCell cell = (GridCell) getComponent(i);
			if (cell.size > configurator.getZoomStep())
				cell.size -= configurator.getZoomStep();
		}
		revalidate();
		repaint();
	}

	public void changeBackgroundColor() {
		Color newColor = JColorChooser.showDialog(this.getParent(), "Set Background Color", cellColor);

		if (newColor == null)
			return;
		else
			cellColor = newColor;

		controller.paintBackground(cellColor);
		setBackground(cellColor.darker());
	}

	public GridCell getCell(int x, int y) {
		return cells[x][y];
	}

	public GridCell getCell(Position pos) {
		return getCell(pos.x, pos.y);
	}

	public GridCell[][] getCells() {
		return cells;
	}
}
