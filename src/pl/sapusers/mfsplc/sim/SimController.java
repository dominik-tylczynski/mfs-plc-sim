package pl.sapusers.mfsplc.sim;

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

		for (int x = 0; x < configurator.getGridSize(); x++) {
			for (int y = 0; y < configurator.getGridSize(); y++) {
				view.add(new PlcCell(x, y));
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
