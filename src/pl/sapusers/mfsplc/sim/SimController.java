package pl.sapusers.mfsplc.sim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;

import pl.sapusers.mfsplc.Configurator;

public class SimController {
	private SimView view;
	private SimModel model;
	private Configurator configurator;
	private HashSet<Plc> selectedPlcs;

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
// create PLC
// TO-DO call PLC dialog
			createPlc(pos);
			deselectPlc();
			selectPlc(model.getPlc(pos));
		} else {
// change PLC

		}
	}

	public void paintPlc(Plc plc) {
		for (int i = 0; i < plc.getCells().size(); i++) {
			view.getCell(plc.getCells().get(i)).setText(Integer.valueOf(i + 1).toString());
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
