package pl.sapusers.mfsplc.sim;

import java.util.ArrayList;

import pl.sapusers.mfsplc.Configurator;

public class SimModel {
	private Configurator configurator;
	private ArrayList<Plc> plcs;

	public SimModel(Configurator configurator) {
		this.configurator = configurator;
		this.plcs = new ArrayList<Plc>();
	}

	public void createPlc(Position pos) {
		plcs.add(new Plc(pos));
	}

	public Plc getPlc(Position pos) {
		for (Plc plc : plcs ) {
			if (plc.containsCell(pos)) return plc;
		}
		
		return null;
	}
	
}
