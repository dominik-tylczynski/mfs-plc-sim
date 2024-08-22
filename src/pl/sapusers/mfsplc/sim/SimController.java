package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.border.Border;

import pl.sapusers.mfsplc.Configurator;

public class SimController implements MouseListener {
	private SimView view;
	private SimModel model;
	private Configurator configurator;
	private HashSet<Plc> selectedPlcs = new HashSet<Plc>();

	public SimController(Configurator configurator) {
		this.configurator = configurator;
		init();
	}

	public void init() {
		model = new SimModel(configurator);
		view = new SimView(configurator, this);
	}

	public SimView getView() {
		return view;
	}

	public SimModel getModel() {
		return model;
	}

	public void createPlc(Position pos) {
		model.addPlc(new Plc(pos, configurator.getPlcColor()));
		selectPlc(model.getPlc(pos));
		paintPlc(model.getPlc(pos));
	}

	public void handleMouseDoubleClick(Position pos) {

	}

	public void handleMouseSingleClick(Position pos) {
		Plc plc = model.getPlc(pos);

		if (plc == null) {
			if (selectedPlcs.size() == 0) {
				// create PLC
// TO-DO call PLC dialog
				deselectPlc();
				createPlc(pos);
			} else if (selectedPlcs.size() == 1) {
				// add cell to PLC
				Plc selectedPlc = (Plc) selectedPlcs.toArray()[0];
				selectedPlc.appendCell(pos);
				paintPlc(selectedPlc);
			}
		} else {
// change PLC

		}
	}

	public void paintPlc(Plc plc) {
		Border border;
		Color color;

		if (selectedPlcs.contains(plc)) {
			border = SimView.BORDER_DOWN;
			color = plc.getColor().darker();
		} else {
			border = SimView.BORDER_UP;
			color = plc.getColor();
		}

		for (int i = 0; i < plc.getCells().size(); i++) {
			GridCell cell = view.getCell(plc.getCells().get(i));
			cell.setText(Integer.valueOf(i + 1).toString());
			cell.setBackground(color);
			cell.setBorder(border);
			cell.setToolTipText("PLC: " + plc.getName());
		}
	}

	public void selectPlc(Plc plc) {
		selectedPlcs.add(plc);
		paintPlc(plc);
	}

	public void deselectPlc(Plc plc) {
		selectedPlcs.remove(plc);
		paintPlc(plc);
	}

	public void deselectPlc() {
		for (Plc plc : model.getPlc()) {
			deselectPlc(plc);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Position pos = ((GridCell) e.getComponent()).pos;
		Plc plc = model.getPlc(pos);

		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
			if (plc == null) {
				if (selectedPlcs.size() == 0) {
					// create PLC
					// TO-DO call PLC dialog
					deselectPlc();
					createPlc(pos);
					paintBackground();
				} else if (selectedPlcs.size() == 1) {
					// add cell to PLC
					Plc selectedPlc = (Plc) selectedPlcs.toArray()[0];
					selectedPlc.appendCell(pos);
					paintPlc(selectedPlc);
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
				if (plc.hasOneCell()) {
					selectedPlcs.remove(plc);
					model.removePlc(plc);
				} else {
					plc.removeCell(pos);
					paintPlc(plc);
				}
				paintBackground(pos);
			}

			return;
		}
	}

	public void paintBackground(Color color) {
		model.setBackgroundColor(color);
		paintBackground();
	}

	public void paintBackground() {
		ArrayList<Plc> plcs = model.getPlc();
		HashSet<Position> occupied = new HashSet<Position>();

		for (Plc plc : plcs) {
			occupied.addAll(plc.getCells());
		}

		HashMap<Position, GridCell> cells = view.getCells();
		for (Position pos : cells.keySet()) {
			if (!occupied.contains(pos)) {
				paintBackground(pos);
			}
		}
	}

	public void paintBackground(Position pos) {
		GridCell cell = view.getCell(pos);

		cell.setBackground(model.getBackgroundColor());
		cell.setBorder(SimView.EMPTY_BORDER);
		cell.setToolTipText(null);
		cell.setText(null);
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

}
