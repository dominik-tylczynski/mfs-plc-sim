package pl.sapusers.mfsplc.sim;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import pl.sapusers.mfsplc.Configurator;

public class SimView extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
	private Configurator configurator;
	private SimController controller;
	private HashMap<Position, GridCell> cells;

	public SimView(Configurator configurator, SimController controller) {
		super();

		this.configurator = configurator;
		this.controller = controller;

		setLayout(new GridBagLayout());
		setBackground(configurator.getCellColor().darker());

		cells = new HashMap<Position, GridCell>();

		GridBagConstraints gbc = new GridBagConstraints();

		for (int x = 0; x < configurator.getGridSize(); x++) {
			for (int y = 0; y < configurator.getGridSize(); y++) {
				gbc.gridx = x;
				gbc.gridy = y;
				gbc.insets = new Insets(1, 1, 1, 1);

				GridCell cell = new GridCell(x, y, configurator.getCellSize(), configurator.getCellColor());
				cells.put(cell.pos, cell);

				cell.setText(Integer.valueOf(x).toString());
				cell.addActionListener(this);
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

	public void setCellEnabled(Position pos, boolean enabled) {
		GridCell cell = cells.get(pos);
		if (cell != null)
			cell.setEnabled(enabled);
	}

	public void setCellSelected(Position pos, boolean selected) {
		GridCell cell = cells.get(pos);
		if (cell != null) {
			cell.setSelected(selected);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getClickCount() == 2 && !((GridCell) e.getComponent()).isSelected()) {
			controller.createPlc(((GridCell) e.getComponent()).pos);
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
		((GridCell) e.getComponent()).setBorder(BorderFactory.createLoweredSoftBevelBorder());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		((GridCell) e.getComponent()).setBorder(BorderFactory.createEmptyBorder());

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
