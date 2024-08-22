package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import pl.sapusers.mfsplc.Configurator;

public class SimView extends JPanel implements MouseListener, MouseMotionListener {
	public static final Border BORDER_UP = BorderFactory.createBevelBorder(BevelBorder.RAISED);
	public static final Border BORDER_DOWN = BorderFactory.createBevelBorder(BevelBorder.LOWERED);

	private Configurator configurator;
	private SimController controller;
	private Color cellColor;
	private HashMap<Position, GridCell> cells;

	public SimView(Configurator configurator, SimController controller) {
		super();

		this.configurator = configurator;
		this.controller = controller;

		cellColor = configurator.getCellColor();

		setLayout(new GridBagLayout());
		setBackground(cellColor.darker());

		cells = new HashMap<Position, GridCell>();

		GridBagConstraints gbc = new GridBagConstraints();

		for (int x = 0; x < configurator.getGridSize(); x++) {
			for (int y = 0; y < configurator.getGridSize(); y++) {
				gbc.gridx = x;
				gbc.gridy = y;
				gbc.insets = new Insets(1, 1, 1, 1);

				GridCell cell = new GridCell(x, y, configurator.getCellSize(), cellColor);
				cells.put(cell.pos, cell);

				cell.addMouseListener(this);
				cell.addMouseMotionListener(this);

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

		Component[] cells = getComponents();

		for (Component cell : cells) {
			cell.setBackground(cellColor);
		}
		setBackground(cellColor.darker());
	}

	public GridCell getCell(Position pos) {
		return cells.get(pos);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		switch (e.getClickCount()) {
		case 2:
			controller.handleMouseDoubleClick(((GridCell) e.getComponent()).pos);
			break;
		case 1:
			controller.handleMouseSingleClick(((GridCell) e.getComponent()).pos);
			break;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
