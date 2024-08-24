package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JComponent;

import pl.sapusers.mfsplc.Configurator;

public class SimController implements MouseListener {
	private Configurator configurator;
	private SimModel model;
	private HashSet<Point> occupiedCells = new HashSet<Point>();
	private HashMap<Point, Plc> pos2Plc = new HashMap<Point, Plc>();
	private HashSet<Plc> selectedPlcs = new HashSet<Plc>();
	private SimView view;

	public SimController(Configurator configurator) {
		this.configurator = configurator;
		init();
	}

	private void createPlc(Point pos) {
		Plc plc = new Plc(pos, configurator.getPlcColor());

		model.addPlc(plc);
		occupiedCells.add(pos);
		pos2Plc.put(pos, plc);
		selectPlc(plc);
		paintPlc(plc);
		view.repaint();
	}

	public void decorateCell(JComponent cell, int x, int y, boolean isSelected) {
		Plc plc = pos2Plc.get(new Point(x, y));

		if (plc == null) {
			cell.setBorder(SimView.EMPTY_BORDER);
			cell.setBackground(model.getBackgroundColor());
		} else {
			if (selectedPlcs.contains(plc)) {
				cell.setBorder(SimView.BORDER_DOWN);
				cell.setBackground(plc.getColor().darker());
			} else {
				cell.setBorder(SimView.BORDER_UP);
				cell.setBackground(plc.getColor());		
			}	
		}
	}

	public void deselectPlc() {
		selectedPlcs.clear();
		view.repaint();
	}

	public void deselectPlc(Plc plc) {
		selectedPlcs.remove(plc);
		view.repaint();
	}

	public Color getBackgroundColor() {
		return model.getBackgroundColor();
	}

	public ArrayList<Point> getCells() {
		ArrayList<Point> cells = new ArrayList<Point>();

		ArrayList<Plc> plcs = model.getPlc();

		for (Plc plc : plcs)
			cells.addAll(plc.getPosition());

		return cells;
	}

	public SimModel getModel() {
		return model;
	}

	public SimView getView() {
		return view;
	}

	public void handleMove(int dir) {
		if (selectedPlcs.size() == 0)
			return;

		// TO-DO verify constraints
		for (Plc plc : selectedPlcs)
			plc.move(dir);

//		paintBackground();

		for (Plc plc : selectedPlcs)
			paintPlc(plc);
	}

	public void init() {
		model = new SimModel(configurator);
		view = new SimView(configurator, this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Point pos = ((GridCell) e.getComponent()).pos;
		Plc plc = model.getPlc(pos);

		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
			if (plc == null) {
				if (selectedPlcs.size() == 0) {
					// create PLC
					// TODO call PLC dialog
					deselectPlc();
					createPlc(pos);
				} else if (selectedPlcs.size() == 1) {
					// add cell to PLC
					appendPosition((Plc) selectedPlcs.toArray()[0], pos);
				}
			} else {
				if (selectedPlcs.contains(plc))
					deselectPlc(plc);
				else
					selectPlc(plc);

				paintPlc(plc);
			}
			return;
		}

		if (e.getClickCount() == 1 && e.getButton() != MouseEvent.BUTTON1) {
			if (plc != null) {
				if (plc.getPositionsCount() == 1) {
					// TODO add warning dialog
					removePosition(plc, pos);
				} else {
					removePosition(plc, pos);
				}
			}

			return;
		}
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
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private void paintPlc(Plc plc) {
		for (int i = 0; i < plc.getPosition().size(); i++) {
			view.setCellText(Integer.valueOf(i + 1).toString(), plc.getPosition().get(i).x, plc.getPosition().get(i).y);
		}
	}

	private void selectPlc(Plc plc) {
		selectedPlcs.add(plc);
		paintPlc(plc);
	}

	public void setBackgroundColor(Color color) {
		if (color != null)
			model.setBackgroundColor(color);
	}

	private void appendPosition(Plc plc, Point pos) {
		plc.appendPosition(pos);
		occupiedCells.add(pos);
		paintPlc(plc);
		view.repaint();
	}

	private void removePosition(Plc plc, Point pos) {
		plc.removePosition(pos);
		occupiedCells.remove(pos);
		pos2Plc.remove(pos);

		if (plc.getPositionsCount() == 0) {
			model.removePlc(plc);
			selectedPlcs.remove(plc);
		} else {
			paintPlc(plc);
		}

		view.repaint();
	}

}
