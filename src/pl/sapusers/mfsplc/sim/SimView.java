package pl.sapusers.mfsplc.sim;

import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

import pl.sapusers.mfsplc.Configurator;

public class SimView extends JPanel {
	private Configurator configurator;

	public SimView(Configurator configurator) {
		super();
		this.configurator = configurator;
		setLayout(new GridBagLayout());
	};

	public void zoomIn() {
		int max = getComponentCount();

		for (int i = 0; i < max; i++) {
			PlcCell cell = (PlcCell) getComponent(i);
			cell.size += configurator.getZoomStep();
		}
		revalidate();
		repaint();
	}

	public void zoomOut() {
		int max = getComponentCount();

		for (int i = 0; i < max; i++) {
			PlcCell cell = (PlcCell) getComponent(i);
			if (cell.size > configurator.getZoomStep())
				cell.size -= configurator.getZoomStep();
		}
		revalidate();
		repaint();
	}

}
