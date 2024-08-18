package pl.sapusers.mfsplc.sim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import pl.sapusers.mfsplc.Configurator;

public class SimController {
	private SimView view;
	private SimModel model;
	private Configurator configurator;

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
		model.createPlc(pos);
	}

	public void handleMouseClick(Position pos) {

	}

	public void handleMouseDoubleClick(Position pos) {
		Plc plc = model.getPlc(pos);

		if (plc == null) {
// create PLC
			// call PLC dialog
			model.createPlc(pos);
		} else {
// change PLC
			
		}
	}

	public void paintPlc(Plc plc) {

	}
	
	public void selectPlc() {
		
	}

}
