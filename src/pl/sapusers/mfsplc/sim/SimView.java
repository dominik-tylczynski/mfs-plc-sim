package pl.sapusers.mfsplc.sim;

import java.awt.GridLayout;

import javax.swing.JPanel;

import pl.sapusers.mfsplc.Configurator;

public class SimView extends JPanel {
	private Configurator configurator;
	
	SimView(Configurator configurator) {
		super();
		this.configurator = configurator;
		setLayout(new GridLayout(configurator.getGridSize(), configurator.getGridSize(), 0, 0));
	};

}
