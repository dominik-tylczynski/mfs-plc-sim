package pl.sapusers.mfsplc.sim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.border.Border;

import pl.sapusers.mfsplc.Configurator;

public class SimController {
	private SimView view;
	private SimModel model;
	private Configurator configurator;
	private HashSet<Plc> selectedPlcs = new HashSet<Plc>();

	public SimController(Configurator configurator) {
		this.configurator = configurator;
		init();
	}

	public void init() {
		view = new SimView(configurator, this);
		model = new SimModel(configurator);

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
				Plc selectedPlc = (Plc) selectedPlcs.toArray()[0];
				selectedPlc.appendCell(pos);
				paintPlc(selectedPlc);
			}
		} else {
// change PLC

		}
	}

	public void paintPlc(Plc plc) {

		Border border = (selectedPlcs.contains(plc) ? SimView.BORDER_DOWN : SimView.BORDER_UP);

		for (int i = 0; i < plc.getCells().size(); i++) {
			GridCell cell = view.getCell(plc.getCells().get(i));
			cell.setText(Integer.valueOf(i + 1).toString());
			cell.setBackground(plc.getColor());
			cell.setBorder(border);
			cell.setToolTipText(plc.getName());
		}
	}

	public void selectPlc(Plc plc) {
		selectedPlcs.add(plc);

		for (Position pos : plc.getCells()) {
			GridCell cell = view.getCell(pos);
			cell.setBackground(plc.getColor().darker());
			cell.setBorder(SimView.BORDER_DOWN);
		}
	}

	public void deselectPlc(Plc plc) {
		for (Position pos : plc.getCells()) {
			GridCell cell = view.getCell(pos);
			cell.setBackground(plc.getColor().brighter());
			cell.setBorder(SimView.BORDER_UP);
		}
	}

	public void deselectPlc() {
		for (Plc plc : model.getPlc()) {
			deselectPlc(plc);
			selectedPlcs.remove(plc);
		}
	}

}
