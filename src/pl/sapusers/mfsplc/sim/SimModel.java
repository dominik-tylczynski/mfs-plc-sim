package pl.sapusers.mfsplc.sim;

import java.util.ArrayList;

import pl.sapusers.mfsplc.Configurator;

public class SimModel {
	private Configurator configurator;
	private ArrayList<Plc> plcs;

	public SimModel(Configurator configurator) {
		this.configurator = configurator;
	}

	public void createPlc(Position pos) {
		plcs.add(new Plc(pos));
	}

}
