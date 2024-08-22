package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.util.ArrayList;

import pl.sapusers.mfsplc.Configurator;

public class SimModel {
// saved attributes
	private ArrayList<Plc> plcs;
	private Color backgroundColor;
	
	
	private Configurator configurator;


	public SimModel(Configurator configurator) {
		this.configurator = configurator;
		this.plcs = new ArrayList<Plc>();
	}

	public void addPlc(Plc plc) {
		plcs.add(plc);
	}

	public Plc getPlc(Position pos) {
		for (Plc plc : plcs ) {
			if (plc.containsCell(pos)) return plc;
		}
		
		return null;
	}
	
	public ArrayList<Plc> getPlc() {
		return plcs;
	}
	
	public void setBackgrounColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
}
