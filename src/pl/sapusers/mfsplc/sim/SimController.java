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
	private Configurator configurator;
	private SimModel model;
	private HashSet<Plc> selectedPlcs = new HashSet<Plc>();
	private SimView view;

	public SimController(Configurator configurator) {
		this.configurator = configurator;
		init();
	}

	public void createPlc(Position pos) {
		model.addPlc(new Plc(pos, configurator.getPlcColor()));
		selectPlc(model.getPlc(pos));
		paintPlc(model.getPlc(pos));
	}

	public void deselectPlc() {
		for (Plc plc : model.getPlc()) {
			deselectPlc(plc);
		}
	}

	public void deselectPlc(Plc plc) {
		selectedPlcs.remove(plc);
		paintPlc(plc);
	}

	public SimModel getModel() {
		return model;
	}

	public SimView getView() {
		return view;
	}

	public ArrayList<Position> getCells() {
		ArrayList<Position> cells = new ArrayList<Position>();
		
		ArrayList<Plc> plcs = model.getPlc();
		
		for (Plc plc : plcs) 
			cells.addAll(plc.getPosition());
		
		return cells;
	} 
	
	public void handleMove(int dir) {
		if (selectedPlcs.size() == 0) return;
		
		// TO-DO verify constraints
		for (Plc plc : selectedPlcs)
			plc.move(dir);
		
		paintBackground();
		
		for (Plc plc : selectedPlcs)
			paintPlc(plc);
	}
	
	public void init() {
		model = new SimModel(configurator);
		view = new SimView(configurator, this);
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
					selectedPlc.appendPosition(pos);
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
				if (plc.hasOnePosition()) {
					selectedPlcs.remove(plc);
					model.removePlc(plc);
				} else {
					plc.removePosition(pos);
					paintPlc(plc);
				}
				paintBackground(pos);
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

	public void paintBackground() {
		GridCell[][] cells = view.getCells();
		
		HashSet<Position> occupied = getOccupiedPositions();

		for (int x = 0; x < cells.length; x++)
			for (int y = 0; y < cells[x].length; y++)
				if (!occupied.contains(cells[x][y].pos))
					paintBackground(cells[x][y].pos);
	}

	public void paintBackground(Color color) {
		model.setBackgroundColor(color);
		paintBackground();
	}

	
	public HashSet<Position> getOccupiedPositions() {
		HashSet<Position> pos = new HashSet<Position>();
		
		for (Plc plc : model.getPlc())
			pos.addAll(plc.getPosition());
		return pos;
			
	}
	
	public void paintBackground(Position pos) {
		GridCell cell = view.getCell(pos.x, pos.y);

		cell.setBackground(model.getBackgroundColor());
		cell.setBorder(SimView.EMPTY_BORDER);
		cell.setToolTipText(null);
		cell.setText(null);
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

		for (int i = 0; i < plc.getPosition().size(); i++) {
			GridCell cell = view.getCell(plc.getPosition().get(i));
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

}
