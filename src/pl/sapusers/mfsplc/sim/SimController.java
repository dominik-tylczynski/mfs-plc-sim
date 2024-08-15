package pl.sapusers.mfsplc.sim;

import java.awt.GridBagConstraints;

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
		view = new SimView(configurator);
		model = new SimModel(configurator);

		GridBagConstraints gbc = new GridBagConstraints();
		
		for (int x = 0; x < configurator.getGridSize(); x++) {
			for (int y = 0; y < configurator.getGridSize(); y++) {
				gbc.gridx = x;
				gbc.gridy = y;
				view.add(new PlcCell(x, y, configurator.getCellSize()), gbc);
			}
		}
	}

	public SimView getView() {
		return view;
	}

	public SimModel getModel() {
		return model;
	}

}
