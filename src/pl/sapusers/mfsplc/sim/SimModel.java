package pl.sapusers.mfsplc.sim;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import pl.sapusers.mfsplc.Configurator;

public class SimModel {
// saved attributes
	private ArrayList<Plc> plcs;
	private Color backgroundColor;
	
	
	private Configurator configurator;


	public SimModel(Configurator configurator) {
		this.configurator = configurator;
		this.backgroundColor = configurator.getCellColor();
		this.plcs = new ArrayList<Plc>();
	}

	public void addPlc(Plc plc) {
		plcs.add(plc);
	}
	
	public void removePlc(Plc plc) {
		plcs.remove(plc);
	}

	public Plc getPlc(Point pos) {
		for (Plc plc : plcs ) {
			if (plc.containsPosition(pos)) return plc;
		}
		
		return null;
	}
	
	public ArrayList<Plc> getPlc() {
		return plcs;
	}
	
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
}
